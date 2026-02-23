package com.example.digitalparking.Dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;


// ServiceOrderResponse.java
public record ServiceOrderResp(
        String orderUuid,
        String serviceName,
        String clientName,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
