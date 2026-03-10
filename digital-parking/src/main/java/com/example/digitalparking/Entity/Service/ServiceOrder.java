package com.example.digitalparking.Entity.Service;

import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Enum.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 36, max = 40)
    private String orderUuid = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private UserEntity client;

    @Column(name = "rate_applied", nullable = false)
    private BigDecimal rateApplied; // The rate at time of order

    @Column(name = "service_type")
    private String serviceType;

    // Car wash fields
    @Column(name = "appointment_date")
    private String appointmentDate;

    @Column(name = "appointment_time")
    private String appointmentTime;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "wash_package")
    private String washPackage;

    // Parking fields
    @Column(name = "parking_date")
    private String parkingDate;

    @Column(name = "selected_slot")
    private String selectedSlot;

    @Column(name = "entry_time")
    private String entryTime;

    @Column(name = "duration")
    private String duration;

    // Amusement fields
    @Column(name = "visit_date")
    private String visitDate;

    @Column(name = "ticket_type")
    private String ticketType;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "addons")
    private String addons; // Comma-separated values

    @Column(name = "duration_hours")
    private Double durationHours; // For HOURLY services

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notes")
    private String notes;

    @Column(name = "order_date")
    private String orderDate;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = OrderStatus.PENDING;
        orderUuid = UUID.randomUUID().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
