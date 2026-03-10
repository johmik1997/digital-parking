package com.example.digitalparking.Dto.Response;


import com.example.digitalparking.Enum.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Builder
public class ServiceOrderResponse {
    private Long id;
    private String serviceName;
    @JsonProperty("orderUuid")
    private String orderUuid;
    private String serviceDescription;
    private String serviceType;
    private String pricingType;
    private String duration;
    private Double durationHours;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Double rateApplied;
    private String appointmentDate;
    private String appointmentTime;
    private String vehiclePlate;
    private String vehicleType;
    private String washPackage;
    private String parkingDate;
    private String selectedSlot;
    private String entryTime;
    private String visitDate;
    private String ticketType;
    private Integer quantity;
    private String addons;
    private String notes;
    private String orderDate;
    private String clientName;
    private String clientEmail;
}
