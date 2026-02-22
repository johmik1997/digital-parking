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
public class Meta {
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_uuid")
    private String orderUuid;

    @JsonProperty("service_type")
    private String serviceType;

    @JsonProperty("user_uuid")
    private String userUuid;
}