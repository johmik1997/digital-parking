package com.example.digitalparking.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParkingAvailabilityResponse {
    private String entryTime;
    private Integer capacity;
    private Long booked;
    private Integer remaining;
    private Boolean available;
    private List<String> reservedSlotKeys;
}
