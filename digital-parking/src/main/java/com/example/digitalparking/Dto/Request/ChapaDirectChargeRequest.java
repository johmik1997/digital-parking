package com.example.digitalparking.Dto.Request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapaDirectChargeRequest {
    private Double amount;
    private String currency;
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("tx_ref")
    private String txRef;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("bank_code")
    private String bankCode;
}