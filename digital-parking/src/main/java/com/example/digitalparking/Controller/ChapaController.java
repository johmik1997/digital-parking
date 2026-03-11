package com.example.digitalparking.Controller;
import com.example.digitalparking.Dto.Request.ChapaPaymentRequest;

import com.example.digitalparking.Service.ChapaService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/chapa") // Clean versioning
public class ChapaController {

    @Autowired
    private ChapaService chapaService;

    @PostMapping("/initialize")
    public ResponseEntity<?> initialize(@RequestBody ChapaPaymentRequest request) {
        return ResponseEntity.ok(chapaService.initializePayment(request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody JsonNode payload,
                                        @RequestHeader(value = "x-chapa-signature", required = false) String sig) {
        chapaService.processWebhook(payload, sig);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{tx_ref}")
    public ResponseEntity<?> verify(@PathVariable("tx_ref") String txRef) {
        return ResponseEntity.ok(chapaService.verifyPayment(txRef));
    }
}