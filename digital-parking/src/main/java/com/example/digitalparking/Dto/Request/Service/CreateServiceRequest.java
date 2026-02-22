package com.example.digitalparking.Dto.Request.Service;

import lombok.Data;

@Data
public class CreateServiceRequest {

    private String name;
    private String description;
    private String pricingType; // HOURLY or FIXED
}