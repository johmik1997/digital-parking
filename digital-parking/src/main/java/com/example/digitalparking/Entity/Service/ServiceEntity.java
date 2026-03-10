package com.example.digitalparking.Entity.Service;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String serviceUUid;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String pricingType; // HOURLY, FIXED

    private Integer slot;

    private BigDecimal currentRate;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (serviceUUid == null) {
            serviceUUid = UUID.randomUUID().toString();
        }
    }
}
