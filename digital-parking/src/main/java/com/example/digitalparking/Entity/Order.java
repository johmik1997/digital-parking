package com.example.digitalparking.Entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "order_uuid", unique = true)
    private String orderUuid;

    @Column(name = "user_uuid")
    private String userUuid;

    @Column(name = "service_uuid")
    private String serviceUuid;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "status")
    private String status;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "payment_tx_ref")
    private String paymentTxRef;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "booking_details", columnDefinition = "TEXT")
    private String bookingDetails;
}