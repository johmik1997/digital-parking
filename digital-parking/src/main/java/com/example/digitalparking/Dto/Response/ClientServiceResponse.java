package com.example.digitalparking.Dto.Response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientServiceResponse {

    private String serviceUuid;
    private String name;
    private String description;
    private String pricingType;
    private Boolean active;
    private BigDecimal currentRate;
}
