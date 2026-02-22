package com.example.digitalparking.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceResponse {

    private String serviceUuid;
    private String name;
    private String description;
    private String pricingType;
    private Boolean active;
    private BigDecimal currentRate;
}
