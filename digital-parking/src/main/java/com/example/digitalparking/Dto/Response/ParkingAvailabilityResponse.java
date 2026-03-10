package com.example.digitalparking.Dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingAvailabilityResponse {
    private String entryTime;
    private Integer capacity;
    private Long booked;
    private Integer remaining;
    private Boolean available;
}
