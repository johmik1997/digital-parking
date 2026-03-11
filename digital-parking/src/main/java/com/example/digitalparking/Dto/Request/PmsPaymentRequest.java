package com.example.digitalparking.Dto.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PmsPaymentRequest {
    private String plateNo;
    private String entryTime;
    private String cashierId;
    private String paymentMethod; // CASH, TELEBIRR, etc.
    private BigDecimal amount;
}
