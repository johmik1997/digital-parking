package com.example.digitalparking.Controller;

import com.example.digitalparking.Dto.Request.ChapaDirectChargeRequest;
import com.example.digitalparking.Dto.Request.ChapaPaymentRequest;
import com.example.digitalparking.Dto.Request.ChapaTransferRequest;
import com.example.digitalparking.Entity.Order;
import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Entity.Transaction;
import com.example.digitalparking.Enum.OrderStatus;
import com.example.digitalparking.Repository.Service.ServiceOrRepository;
import com.example.digitalparking.Repository.Service.ServiceOrderRepository;
import com.example.digitalparking.Service.ServiceOrderService;
import com.example.digitalparking.Service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chapa")
@CrossOrigin(origins = "*")
public class ChapController {

    private static final Logger logger = LoggerFactory.getLogger(ChapController.class);

    @Value("${chapa.secret.key}")
    private String chapaSecretKey;

    @Value("${chapa.transaction.url:https://api.chapa.co/v1/transaction/initialize}")
    private String transactionUrl;

    @Value("${chapa.verification.url:https://api.chapa.co/v1/transaction/verify}")
    private String verificationUrl;

    @Value("${chapa.webhook.secret:MedTestGoCbhi}")
    private String webhookSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ServiceOrRepository orderService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Initialize Payment with Chapa
     */
    @PostMapping("/initialize")
    public ResponseEntity<?> initializePayment(@RequestBody ChapaPaymentRequest request) {
        try {
            if (request.getAmount() == null || request.getEmail() == null || request.getTxRef() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(chapaSecretKey);

            Map<String, Object> chapaRequest = new HashMap<>();
            chapaRequest.put("amount", request.getAmount());
            chapaRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "ETB");
            chapaRequest.put("email", request.getEmail());
            chapaRequest.put("first_name", request.getFirstName());
            chapaRequest.put("last_name", request.getLastName());
            chapaRequest.put("tx_ref", request.getTxRef());
            chapaRequest.put("callback_url", request.getCallbackUrl());
            chapaRequest.put("return_url", request.getReturnUrl());

            if (request.getMeta() != null) {
                chapaRequest.put("meta", Map.of(
                        "order_uuid", request.getMeta().getOrderUuid(),
                        "user_uuid", request.getMeta().getUserUuid()
                ));
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(chapaRequest, headers);
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(transactionUrl, entity, JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Save Transaction as Pending
                Transaction transaction = new Transaction();
                transaction.setTxRef(request.getTxRef());
                transaction.setAmount(request.getAmount());
                transaction.setStatus("pending");
                transaction.setOrderUuid(request.getMeta() != null ? request.getMeta().getOrderUuid() : null);
                transaction.setCreatedAt(LocalDateTime.now());
                transactionService.saveTransaction(transaction);

                return ResponseEntity.ok(response.getBody());
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", "Chapa initialization failed"));

        } catch (Exception e) {
            logger.error("Chapa init error: ", e);
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Webhook handler with CORRECT Hex Signature Verification
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> chapaWebhook(
            @RequestBody JsonNode payload,
            @RequestHeader(value = "x-chapa-signature", required = false) String signature) {

        if (!verifyWebhookSignature(payload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String txRef = payload.path("tx_ref").asText();
            String status = payload.path("status").asText();

            logger.info("Webhook received for {}: Status {}", txRef, status);

            Transaction transaction = transactionService.findByTxRef(txRef);
            if (transaction != null) {
                transaction.setStatus(status.toLowerCase());
                transaction.setWebhookReceivedAt(LocalDateTime.now());
                transactionService.updateTransaction(transaction);

                if ("success".equalsIgnoreCase(status) && transaction.getOrderUuid() != null) {
                    finalizeOrder(transaction.getOrderUuid(), txRef);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Webhook processing failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Manual Verification Endpoint
     */
    @GetMapping("/verify/{tx_ref}")
    public ResponseEntity<?> verifyPayment(@PathVariable("tx_ref") String txRef) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(chapaSecretKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    verificationUrl + "/" + txRef, HttpMethod.GET, entity, JsonNode.class);

            if (response.getBody() != null && "success".equalsIgnoreCase(response.getBody().path("status").asText())) {
                String remoteStatus = response.getBody().path("data").path("status").asText();

                Transaction transaction = transactionService.findByTxRef(txRef);
                if (transaction != null) {
                    transaction.setStatus(remoteStatus.toLowerCase());
                    transaction.setVerifiedAt(LocalDateTime.now());
                    transactionService.updateTransaction(transaction);

                    if ("success".equalsIgnoreCase(remoteStatus)) {
                        finalizeOrder(transaction.getOrderUuid(), txRef);
                    }
                }
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private void finalizeOrder(String orderUuid, String txRef) {
        Optional<ServiceOrder> order = orderService.findByOrderUuid(orderUuid);
        if (order != null && !"paid".equalsIgnoreCase(String.valueOf(order.get().getStatus()))) {
            order.get().setStatus(OrderStatus.COMPLETED);
            logger.info("Order {} marked as PAID", orderUuid);
        }
    }

    /**
     * FIXED: Hex Signature Verification
     */
    private boolean verifyWebhookSignature(JsonNode payload, String signature) {
        if (signature == null) return false;
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hashBytes = sha256Hmac.doFinal(payload.toString().getBytes(StandardCharsets.UTF_8));

            // Convert to HEX string (Chapa requirement)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return MessageDigest.isEqual(hexString.toString().getBytes(), signature.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
}