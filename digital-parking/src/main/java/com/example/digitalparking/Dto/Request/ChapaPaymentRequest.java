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
public class ChapaPaymentRequest {
    private Double amount;
    private String currency;
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("tx_ref")
    private String txRef;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("return_url")
    private String returnUrl;

    private Customization customization;
    private Meta meta;
}