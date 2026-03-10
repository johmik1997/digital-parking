package com.example.digitalparking.Dto.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PmsPendingVehicleResponse {
    private String plateNo;
    private String entryTime;
    private String exitTime;
    private String cashierId;
    private String status;
    private String lastUpdated;
}
