package com.example.digitalparking.Dto.Request.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ServiceOrderRequest.java
public record ServiceOrderReq(
        String serviceUuid,
        Long clientId,
        Double durationHours,
        String notes
) {}
