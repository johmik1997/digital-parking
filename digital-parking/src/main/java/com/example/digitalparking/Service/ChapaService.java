package com.example.digitalparking.Service;


import com.example.digitalparking.Dto.Request.ChapaPaymentRequest;
import com.example.digitalparking.Entity.Transaction;
import com.fasterxml.jackson.databind.JsonNode;

public interface ChapaService {
    JsonNode initializePayment(ChapaPaymentRequest request);
    JsonNode verifyPayment(String txRef);
    void processWebhook(JsonNode payload, String signature);
}