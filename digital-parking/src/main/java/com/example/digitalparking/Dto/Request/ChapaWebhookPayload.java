package com.example.digitalparking.Dto.Request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChapaWebhookPayload {
    @JsonProperty("tx_ref")
    private String txRef;

    private String status;
    private Double amount;
    private String currency;
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}