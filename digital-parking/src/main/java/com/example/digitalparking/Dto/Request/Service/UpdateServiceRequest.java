package com.example.digitalparking.Dto.Request.Service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateServiceRequest {

    private String name;
    private String description;
    private String pricingType; // HOURLY or FIXED
    private Integer slot;
    private BigDecimal rate;
}
