package com.example.digitalparking.Controller;

import com.example.digitalparking.Dto.Request.ChapaDirectChargeRequest;
import com.example.digitalparking.Dto.Request.ChapaPaymentRequest;
import com.example.digitalparking.Dto.Request.ChapaTransferRequest;
import com.example.digitalparking.Entity.Order;
import com.example.digitalparking.Entity.Transaction;
import com.example.digitalparking.Service.OrderService;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chapa")
@CrossOrigin(origins = "*")
public class ChapController {

    private static final Logger logger = LoggerFactory.getLogger(ChapController.class);

    // Your Chapa Configuration
    @Value("${chapa.secret.key:CHASECK-KavPE1HMF0tOEnPnMJudcANRq8x4aGIR}")
    private String chapaSecretKey;

    @Value("${chapa.transaction.url:https://api.chapa.co/v1/transaction/initialize}")
    private String transactionUrl;

    @Value("${chapa.verification.url:https://api.chapa.co/v1/transaction/verify}")
    private String verificationUrl;

    @Value("${chapa.transfer.url:https://api.chapa.co/v1/transfers}")
    private String transferUrl;

    @Value("${chapa.direct.charge.url:https://api.chapa.co/v1/charges}")
    private String directChargeUrl;

    @Value("${chapa.transaction.verification.url:https://api.chapa.co/v1/transfers/verify}")
    private String transactionVerificationUrl;

    @Value("${chapa.webhook.secret:MedTestGoCbhi}")
    private String webhookSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/initialize")
    public ResponseEntity<?> initializePayment(@RequestBody ChapaPaymentRequest request) {
        logger.info("Initializing Chapa payment for tx_ref: {}", request.getTxRef());

        try {
            // ==========================
            // Validate Required Fields
            // ==========================
            if (request.getAmount() == null ||
                    request.getEmail() == null ||
                    request.getTxRef() == null) {

                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Missing required payment fields: amount, email, or tx_ref"
                ));
            }

            // ==========================
            // Prepare Headers
            // ==========================
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(chapaSecretKey); // DO NOT prefix manually with "Bearer"

            // ==========================
            // Build Chapa Request
            // ==========================
            Map<String, Object> chapaRequest = new HashMap<>();
            chapaRequest.put("amount", String.format("%.2f", request.getAmount())); // string with 2 decimals
            chapaRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "ETB");
            chapaRequest.put("email", request.getEmail());
            chapaRequest.put("first_name", request.getFirstName());
            chapaRequest.put("last_name", request.getLastName());
            chapaRequest.put("phone_number", request.getPhoneNumber());
            chapaRequest.put("tx_ref", request.getTxRef());
            chapaRequest.put("callback_url", request.getCallbackUrl());
            chapaRequest.put("return_url", request.getReturnUrl());
            chapaRequest.put("payment_options", "card,telebirr,birr,bank"); // required in some accounts

            // Customization
            if (request.getCustomization() != null) {
                Map<String, String> customization = new HashMap<>();
                customization.put("title", request.getCustomization().getTitle());
                customization.put("description", request.getCustomization().getDescription());
                customization.put("logo", request.getCustomization().getLogo());
                chapaRequest.put("customization", customization);
            }

            // Meta
            if (request.getMeta() != null) {
                Map<String, String> meta = new HashMap<>();
                meta.put("order_id", request.getMeta().getOrderId());
                meta.put("order_uuid", request.getMeta().getOrderUuid());
                meta.put("service_type", request.getMeta().getServiceType());
                meta.put("user_uuid", request.getMeta().getUserUuid());
                chapaRequest.put("meta", meta);
            }

            // ==========================
            // Log Request Payload
            // ==========================
            logger.info("Chapa request payload: {}", objectMapper.writeValueAsString(chapaRequest));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(chapaRequest, headers);

            // ==========================
            // Call Chapa API
            // ==========================
            ResponseEntity<String> chapaResponse = restTemplate.exchange(
                    transactionUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            logger.info("Raw Chapa response: {}", chapaResponse.getBody());

            if (chapaResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                        "status", "error",
                        "message", "Empty response from Chapa"
                ));
            }

            JsonNode body = objectMapper.readTree(chapaResponse.getBody());
            String status = body.path("status").asText();

            if (!"success".equalsIgnoreCase(status)) {
                String message = body.path("message").asText("Payment initialization failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "status", "error",
                        "message", message,
                        "raw_response", chapaResponse.getBody() // include raw response for debugging
                ));
            }

            String checkoutUrl = body.path("data").path("checkout_url").asText(null);

            if (checkoutUrl == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                        "status", "error",
                        "message", "Checkout URL missing from Chapa response",
                        "raw_response", chapaResponse.getBody()
                ));
            }

            // ==========================
            // Save Transaction
            // ==========================
            Transaction transaction = new Transaction();
            transaction.setTxRef(request.getTxRef());
            transaction.setAmount(request.getAmount());
            transaction.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ETB");
            transaction.setStatus("pending");
            transaction.setCustomerEmail(request.getEmail());
            transaction.setCustomerName(request.getFirstName() + " " + request.getLastName());
            transaction.setCustomerPhone(request.getPhoneNumber());
            transaction.setOrderUuid(request.getMeta() != null ? request.getMeta().getOrderUuid() : null);
            transaction.setServiceType(request.getMeta() != null ? request.getMeta().getServiceType() : null);
            transaction.setCreatedAt(LocalDateTime.now());
            transactionService.saveTransaction(transaction);

            // ==========================
            // Return Clean Response
            // ==========================
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Payment initialized successfully",
                    "data", Map.of(
                            "checkout_url", checkoutUrl,
                            "tx_ref", request.getTxRef()
                    )
            ));

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Chapa API error, status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "status", "error",
                    "message", e.getResponseBodyAsString()
            ));
        } catch (Exception e) {
            logger.error("Chapa payment initialization error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Internal server error while initializing payment"
            ));
        }
    }


    /**
     * Verify payment with Chapa
     * URL: https://api.chapa.co/v1/transaction/verify/{tx_ref}
     */
    @GetMapping("/verify/{tx_ref}")
    public ResponseEntity<?> verifyPayment(@PathVariable("tx_ref") String txRef) {
        logger.info("Verifying Chapa payment for tx_ref: {}", txRef);

        try {
            String url = verificationUrl + "/" + txRef;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(chapaSecretKey);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            // Update transaction status in database
            if (response.getBody() != null) {
                String status = response.getBody().path("data").path("status").asText();

                Transaction transaction = transactionService.findByTxRef(txRef);
                if (transaction != null) {
                    transaction.setStatus(status);
                    transaction.setVerifiedAt(LocalDateTime.now());
                    transactionService.updateTransaction(transaction);

                    // If payment successful, update order status
                    if ("success".equals(status) && transaction.getOrderUuid() != null) {
                        Order order = orderService.findByOrderUuid(transaction.getOrderUuid());
                        if (order != null) {
                            order.setStatus("paid");
                            order.setPaymentTxRef(txRef);
                            order.setPaidAt(LocalDateTime.now());
                            orderService.updateOrder(order);

                            logger.info("Order {} marked as paid", order.getOrderUuid());
                        }
                    }
                }
            }

            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Chapa verification API error: {}", e.getMessage());
            try {
                JsonNode errorBody = objectMapper.readTree(e.getResponseBodyAsString());
                return ResponseEntity.status(e.getStatusCode())
                        .body(Map.of(
                                "status", "error",
                                "message", errorBody.path("message").asText("Payment verification failed")
                        ));
            } catch (Exception ex) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(Map.of(
                                "status", "error",
                                "message", "Payment verification failed"
                        ));
            }
        } catch (Exception e) {
            logger.error("Chapa payment verification error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Payment verification failed"
                    ));
        }
    }

    /**
     * Webhook handler for Chapa
     * Uses webhook secret: MedTestGoCbhi
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> chapaWebhook(
            @RequestBody JsonNode payload,
            @RequestHeader(value = "x-chapa-signature", required = false) String signature) {

        logger.info("Received Chapa webhook");

        try {
            // Verify webhook signature using your webhook secret
            if (!verifyWebhookSignature(payload, signature)) {
                logger.warn("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "error", "message", "Invalid signature"));
            }

            // Extract data from payload
            String txRef = payload.path("tx_ref").asText();
            String status = payload.path("status").asText();
            double amount = payload.path("amount").asDouble();
            String currency = payload.path("currency").asText();
            String email = payload.path("email").asText();
            String firstName = payload.path("first_name").asText();
            String lastName = payload.path("last_name").asText();
            String phoneNumber = payload.path("phone_number").asText();

            logger.info("Webhook received - TxRef: {}, Status: {}", txRef, status);

            // Update transaction status
            Transaction transaction = transactionService.findByTxRef(txRef);
            if (transaction != null) {
                transaction.setStatus(status);
                transaction.setWebhookReceivedAt(LocalDateTime.now());
                transaction.setWebhookPayload(payload.toString());
                transactionService.updateTransaction(transaction);

                // If payment successful, update order status
                if ("success".equals(status) && transaction.getOrderUuid() != null) {
                    Order order = orderService.findByOrderUuid(transaction.getOrderUuid());
                    if (order != null) {
                        order.setStatus("paid");
                        order.setPaymentTxRef(txRef);
                        order.setPaidAt(LocalDateTime.now());
                        orderService.updateOrder(order);

                        logger.info("Order {} marked as paid via webhook", order.getOrderUuid());
                    }
                }
            } else {
                // Create new transaction if not found (webhook arrived before our callback)
                Transaction newTransaction = new Transaction();
                newTransaction.setTxRef(txRef);
                newTransaction.setAmount(amount);
                newTransaction.setCurrency(currency);
                newTransaction.setStatus(status);
                newTransaction.setCustomerEmail(email);
                newTransaction.setCustomerName(firstName + " " + lastName);
                newTransaction.setCustomerPhone(phoneNumber);
                newTransaction.setWebhookReceivedAt(LocalDateTime.now());
                newTransaction.setWebhookPayload(payload.toString());
                newTransaction.setCreatedAt(LocalDateTime.now());

                transactionService.saveTransaction(newTransaction);
                logger.info("Created new transaction from webhook: {}", txRef);
            }

            return ResponseEntity.ok(Map.of("status", "success"));

        } catch (Exception e) {
            logger.error("Chapa webhook processing error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Webhook processing failed"));
        }
    }

    /**
     * Initialize transfer with Chapa
     * URL: https://api.chapa.co/v1/transfers
     */
    @PostMapping("/transfer/initialize")
    public ResponseEntity<?> initializeTransfer(@RequestBody ChapaTransferRequest request) {
        logger.info("Initializing Chapa transfer for account: {}", request.getAccountNumber());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(chapaSecretKey);

            Map<String, Object> transferRequest = new HashMap<>();
            transferRequest.put("amount", request.getAmount());
            transferRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "ETB");
            transferRequest.put("account_name", request.getAccountName());
            transferRequest.put("account_number", request.getAccountNumber());
            transferRequest.put("bank_code", request.getBankCode());
            transferRequest.put("reference", request.getReference());
            transferRequest.put("narration", request.getNarration());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(transferRequest, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    transferUrl,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            logger.error("Chapa transfer initialization error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Transfer initialization failed"
                    ));
        }
    }

    /**
     * Verify transfer with Chapa
     * URL: https://api.chapa.co/v1/transfers/verify
     */
    @GetMapping("/transfer/verify/{reference}")
    public ResponseEntity<?> verifyTransfer(@PathVariable("reference") String reference) {
        logger.info("Verifying Chapa transfer for reference: {}", reference);

        try {
            String url = transactionVerificationUrl + "/" + reference;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(chapaSecretKey);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            logger.error("Chapa transfer verification error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Transfer verification failed"
                    ));
        }
    }

    /**
     * Direct charge with Chapa
     * URL: https://api.chapa.co/v1/charges
     */
    @PostMapping("/direct-charge")
    public ResponseEntity<?> directCharge(@RequestBody ChapaDirectChargeRequest request) {
        logger.info("Processing direct charge for tx_ref: {}", request.getTxRef());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(chapaSecretKey);

            Map<String, Object> chargeRequest = new HashMap<>();
            chargeRequest.put("amount", request.getAmount());
            chargeRequest.put("currency", request.getCurrency() != null ? request.getCurrency() : "ETB");
            chargeRequest.put("email", request.getEmail());
            chargeRequest.put("phone_number", request.getPhoneNumber());
            chargeRequest.put("tx_ref", request.getTxRef());
            chargeRequest.put("payment_method", request.getPaymentMethod());
            chargeRequest.put("bank_code", request.getBankCode());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(chargeRequest, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    directChargeUrl,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            logger.error("Chapa direct charge error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Direct charge failed"
                    ));
        }
    }

    /**
     * Verify webhook signature using your webhook secret: MedTestGoCbhi
     */
    private boolean verifyWebhookSignature(JsonNode payload, String signature) {
        if (signature == null || signature.isEmpty()) {
            return false;
        }

        try {
            String payloadString = payload.toString();

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            sha256Hmac.init(secretKey);

            byte[] hashBytes = sha256Hmac.doFinal(payloadString.getBytes(StandardCharsets.UTF_8));
            String computedHash = Base64.getEncoder().encodeToString(hashBytes);

            boolean isValid = computedHash.equals(signature);
            logger.info("Webhook signature verification: {}", isValid ? "VALID" : "INVALID");

            return isValid;
        } catch (Exception e) {
            logger.error("Signature verification failed: ", e);
            return false;
        }
    }
}