package com.example.digitalparking.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping
    public ResponseEntity<DemoResponse> getDemoSpec() {
        DemoResponse response = new DemoResponse(
                "Smart Parking System (SPS)",
                "Meskel Square Parking Facility • Addis Ababa • Demo Procurement Portal",
                "Capacity: 1,400+ vehicles across underground basements and surrounding lots.",
                List.of(
                        new Stat("Avg. Entry/Exit", "<2s"),
                        new Stat("LPR Accuracy", "≥98%"),
                        new Stat("Slots Covered", "1,400+"),
                        new Stat("Power Backup", "UPS + surge")
                ),
                List.of(
                        new Section("Core Features", List.of(
                                "Automated entry/exit with boom barriers and loop detectors",
                                "Ticketless QR entry/exit via mobile app, web, or kiosks",
                                "Real-time slot detection with zone guidance",
                                "Digital signage at entry, levels, and exits",
                                "User portal for booking, navigation, and cashless payment"
                        )),
                        new Section("Operations & Analytics", List.of(
                                "Admin dashboard for occupancy, revenue, and audit trails",
                                "Operator console for manual overrides and exception handling",
                                "Custom reporting and export (Excel/PDF)",
                                "Valet mode support with QR-based flows"
                        )),
                        new Section("Integration", List.of(
                                "CCTV and access control integration",
                                "Payment gateways (Telebirr, CBE Birr, bank cards)",
                                "PMS data migration and API connectivity",
                                "Ethiopian fiscal and taxation reporting compatibility"
                        )),
                        new Section("Infrastructure", List.of(
                                "Hybrid cloud-local architecture with offline fallback",
                                "TLS encryption with audit logging and 2FA",
                                "Amharic + English accessibility",
                                "UPS for control room, gates, and servers"
                        ))
                ),
                List.of(
                        new TimelineItem("Mobilization", "Within 1 week of signing"),
                        new TimelineItem("Full Deployment", "≤45 days"),
                        new TimelineItem("Live Trial", "14–30 days before handover")
                ),
                new ContactInfo(
                        "Meskel Square Parking Facility",
                        "Addis Ababa, Ethiopia",
                        "procurement@sps-demo.et",
                        "+251 11 000 0000"
                )
        );

        return ResponseEntity.ok(response);
    }

    public record DemoResponse(
            String title,
            String subtitle,
            String overview,
            List<Stat> stats,
            List<Section> sections,
            List<TimelineItem> timeline,
            ContactInfo contact
    ) {}

    public record Stat(String label, String value) {}

    public record Section(String title, List<String> items) {}

    public record TimelineItem(String phase, String target) {}

    public record ContactInfo(String facility, String address, String email, String phone) {}
}
