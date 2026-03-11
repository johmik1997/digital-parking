package com.example.digitalparking.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PmsPaymentResponse {
    private Long id;
    private String plateNo;
    private String entryTime;
    private String exitTime;
    private String cashierId;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String createdAt;
}
