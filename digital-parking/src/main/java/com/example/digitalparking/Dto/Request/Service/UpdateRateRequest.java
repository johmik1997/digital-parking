package com.example.digitalparking.Dto.Request.Service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateRateRequest {

    private BigDecimal rate;
}