package com.example.digitalparking.Entity.Service;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceRate> rates;

    // Add this field
    @Transient
    private ServiceRate currentRate;

    // Add getter and setter
    public ServiceRate getCurrentRate() {
        return currentRate;
    }

    @PrePersist
    public void prePersist() {
        if (serviceUUid == null) {
            serviceUUid = UUID.randomUUID().toString();
        }
    }
    public void setCurrentRate(ServiceRate currentRate) {
        this.currentRate = currentRate;
    }
}

