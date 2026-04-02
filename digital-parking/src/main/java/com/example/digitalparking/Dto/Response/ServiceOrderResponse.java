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
    private String entranceName;
    private String parkingLevelType;
    private String parkingLevelCode;
    private String parkingZone;
    private String selectedSlot;
    private String parkingLocationDisplay;
    private String googleMapsUrl;
    private String navigationInstructions;
    private String scheduledEntryTime;
    private String entryTime;
    private LocalDateTime completedAt;
    private String visitDate;
    private String ticketType;
    private Integer quantity;
    private String addons;
    private String notes;
    private String orderDate;
    private String clientName;
    private String clientEmail;
    private Double bookedAmount;
    private Double prepaidAmount;
    private Double overtimeAmount;
    private Double amountDueNow;
    private Integer bookedMinutes;
    private Integer elapsedMinutes;
    private Integer lateMinutes;
    private Integer overtimeMinutes;
    private Boolean prepaid;
    private String arrivalWindowStatus;
    private String paymentStatusLabel;
    private String plannedExitTime;
}
