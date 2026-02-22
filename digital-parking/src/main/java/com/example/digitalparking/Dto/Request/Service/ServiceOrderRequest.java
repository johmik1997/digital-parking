package com.example.digitalparking.Dto.Request.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderRequest {

    @NotNull(message = "Service UUID is required")
    @JsonProperty("serviceUuid")
    private String serviceUuid;

    @JsonProperty("serviceType")
    private String serviceType;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("totalAmount")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    // Car Wash Fields
    @JsonProperty("appointmentDate")
    private String appointmentDate;

    @JsonProperty("appointmentTime")
    private String appointmentTime;

    @JsonProperty("vehiclePlate")
    private String vehiclePlate;

    @JsonProperty("vehicleType")
    private String vehicleType;

    @JsonProperty("washPackage")
    private String washPackage;

    // Parking Fields
    @JsonProperty("parkingDate")
    private String parkingDate;

    @JsonProperty("selectedSlot")
    private String selectedSlot;

    @JsonProperty("entryTime")
    private String entryTime;

    @JsonProperty("duration")
    private String duration;

    // Amusement Fields
    @JsonProperty("visitDate")
    private String visitDate;

    @JsonProperty("ticketType")
    private String ticketType;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("addons")
    private List<String> addons;

    // Common Fields
    @JsonProperty("notes")
    private String notes;

    @JsonProperty("orderDate")
    private String orderDate;

    @JsonProperty("createdAt")
    private String createdAt;
}