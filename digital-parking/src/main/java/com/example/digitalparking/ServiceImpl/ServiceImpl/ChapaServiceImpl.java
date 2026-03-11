package com.example.digitalparking.ServiceImpl.ServiceImpl;
import com.example.digitalparking.Dto.Request.ChapaPaymentRequest;
import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Entity.Transaction;
import com.example.digitalparking.Enum.OrderStatus;
import com.example.digitalparking.Repository.Service.ServiceOrRepository;
import com.example.digitalparking.Repository.TransactionRepository;
import com.example.digitalparking.Service.ChapaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ChapaServiceImpl implements ChapaService {

    private static final Logger logger = LoggerFactory.getLogger(ChapaServiceImpl.class);

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
    private TransactionRepository transactionRepository;

    @Autowired
    private ServiceOrRepository orderRepository;

    @Override
    public JsonNode initializePayment(ChapaPaymentRequest request) {
        // 1. Validate Order and Set Amount
        if (request.getMeta() != null && request.getMeta().getOrderUuid() != null) {
            ServiceOrder order = orderRepository.findByOrderUuid(request.getMeta().getOrderUuid())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

            if (order.getTotalAmount() != null) {
                request.setAmount(order.getTotalAmount().doubleValue());
            }
        }

        // 2. Prepare Chapa API Request Body
        Map<String, Object> body = new HashMap<>();
        body.put("amount", request.getAmount());
        body.put("currency", request.getCurrency() != null ? request.getCurrency() : "ETB");
        body.put("email", request.getEmail());
        body.put("first_name", request.getFirstName());
        body.put("last_name", request.getLastName());
        body.put("phone_number", request.getPhoneNumber());
        body.put("tx_ref", request.getTxRef());
        body.put("callback_url", request.getCallbackUrl());
        body.put("return_url", request.getReturnUrl());

        // Clean up Meta: Ensure nested objects are handled correctly by Chapa
        if (request.getMeta() != null) {
            body.put("meta", request.getMeta());
        }

        // 3. Authorization Header
        if (chapaSecretKey == null || chapaSecretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chapa Secret Key is missing in properties");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String authHeader = chapaSecretKey.startsWith("Bearer ") ? chapaSecretKey : "Bearer " + chapaSecretKey;
        headers.set("Authorization", authHeader);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 4. Execute Call with Specific Exception Handling
        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(transactionUrl, entity, JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                saveInitialTransaction(request);
                return response.getBody();
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Chapa failed with status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            // This catches 400 Bad Request, 401 Unauthorized, 409 Conflict
            logger.error("Chapa API Error: {} - Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            // Return the actual Chapa error message to your Swagger/Frontend
            throw new ResponseStatusException(e.getStatusCode(), "Chapa Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Critical Failure in initializePayment: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error: " + e.getMessage());
        }
    }
    @Override
    public void processWebhook(JsonNode payload, String signature) {
        if (!verifyWebhookSignature(payload, signature)) {
            logger.error("Invalid webhook signature received");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Signature");
        }

        String txRef = payload.path("tx_ref").asText();
        String status = payload.path("status").asText();

        transactionRepository.findByTxRef(txRef).ifPresent(transaction -> {
            transaction.setStatus(status.toLowerCase());
            transaction.setWebhookReceivedAt(LocalDateTime.now());
            transaction.setWebhookPayload(payload.toString());
            transactionRepository.save(transaction);

            if ("success".equalsIgnoreCase(status)) {
                finalizeOrder(transaction.getOrderUuid());
            }
        });
    }

    @Override
    public JsonNode verifyPayment(String txRef) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(chapaSecretKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                verificationUrl + "/" + txRef, HttpMethod.GET, entity, JsonNode.class);

        if (response.getBody() != null && "success".equalsIgnoreCase(response.getBody().path("status").asText())) {
            String status = response.getBody().path("data").path("status").asText();
            updateTransactionStatus(txRef, status, response.getBody().toString());
        }

        return response.getBody();
    }

    private void saveInitialTransaction(ChapaPaymentRequest request) {
        Transaction transaction = new Transaction();
        transaction.setTxRef(request.getTxRef());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("pending");
        transaction.setCustomerEmail(request.getEmail());
        transaction.setOrderUuid(request.getMeta() != null ? request.getMeta().getOrderUuid() : null);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private void updateTransactionStatus(String txRef, String status, String rawPayload) {
        transactionRepository.findByTxRef(txRef).ifPresent(t -> {
            t.setStatus(status.toLowerCase());
            t.setVerifiedAt(LocalDateTime.now());
            t.setWebhookPayload(rawPayload);
            transactionRepository.save(t);
            if ("success".equalsIgnoreCase(status)) finalizeOrder(t.getOrderUuid());
        });
    }

    private void finalizeOrder(String orderUuid) {
        orderRepository.findByOrderUuid(orderUuid).ifPresent(order -> {
            if (order.getStatus() != OrderStatus.COMPLETED) {
                order.setStatus(OrderStatus.COMPLETED);
                order.setCompletedAt(LocalDateTime.now());
                orderRepository.save(order);
                logger.info("Order {} finalized via Chapa", orderUuid);
            }
        });
    }

    private boolean verifyWebhookSignature(JsonNode payload, String signature) {
        if (signature == null) return false;
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hashBytes = sha256Hmac.doFinal(payload.toString().getBytes(StandardCharsets.UTF_8));

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