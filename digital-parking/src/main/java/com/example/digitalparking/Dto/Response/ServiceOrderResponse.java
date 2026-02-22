package com.example.digitalparking.Dto.Response;


import com.example.digitalparking.Enum.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ServiceOrderResponse {
    private Long id;
    private String serviceName;
    private String OrderUuid;
    private String serviceDescription;
    private String pricingType;
    private Double durationHours;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String rateUuid;
    private BigDecimal rateApplied;
    private String clientName;
    private String clientEmail;
}
