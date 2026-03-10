package com.example.digitalparking.Controller;

import com.example.digitalparking.Dto.Response.PmsPendingVehicleResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pms")
public class PmsController {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Random RNG = new Random();

    private static final List<PmsPendingVehicleResponse> PENDING = new ArrayList<>(List.of(
            PmsPendingVehicleResponse.builder()
                    .plateNo("5-67887")
                    .entryTime(LocalDateTime.now().minusMinutes(220).format(ISO))
                    .exitTime(null)
                    .cashierId("YH989")
                    .status("PENDING")
                    .lastUpdated(LocalDateTime.now().minusMinutes(1).format(ISO))
                    .build(),
            PmsPendingVehicleResponse.builder()
                    .plateNo("2-12345")
                    .entryTime(LocalDateTime.now().minusMinutes(205).format(ISO))
                    .exitTime(null)
                    .cashierId("YH989")
                    .status("PENDING")
                    .lastUpdated(LocalDateTime.now().minusMinutes(1).format(ISO))
                    .build(),
            PmsPendingVehicleResponse.builder()
                    .plateNo("3-98765")
                    .entryTime(LocalDateTime.now().minusMinutes(45).format(ISO))
                    .exitTime(null)
                    .cashierId("YH989")
                    .status("PENDING")
                    .lastUpdated(LocalDateTime.now().minusMinutes(1).format(ISO))
                    .build()
    ));

    @GetMapping("/pending")
    public List<PmsPendingVehicleResponse> getPendingVehicles(
            @RequestParam(defaultValue = "2") int sinceMinutes,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean refresh
    ) {
        if (refresh) {
            PENDING.add(generateRandomPending());
        }

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(Math.max(sinceMinutes, 0));

        return PENDING.stream()
                .filter(v -> "PENDING".equalsIgnoreCase(v.getStatus()))
                .filter(v -> {
                    try {
                        LocalDateTime updated = LocalDateTime.parse(v.getLastUpdated(), ISO);
                        return updated.isAfter(cutoff);
                    } catch (Exception e) {
                        return true;
                    }
                })
                .sorted(Comparator.comparing(PmsPendingVehicleResponse::getLastUpdated).reversed())
                .limit(Math.max(limit, 1))
                .collect(Collectors.toList());
    }

    private PmsPendingVehicleResponse generateRandomPending() {
        int prefix = RNG.nextInt(9) + 1;
        int suffix = RNG.nextInt(90000) + 10000;
        return PmsPendingVehicleResponse.builder()
                .plateNo(prefix + "-" + suffix)
                .entryTime(LocalDateTime.now().minusMinutes(RNG.nextInt(120)).format(ISO))
                .exitTime(null)
                .cashierId("YH989")
                .status("PENDING")
                .lastUpdated(LocalDateTime.now().format(ISO))
                .build();
    }
}
