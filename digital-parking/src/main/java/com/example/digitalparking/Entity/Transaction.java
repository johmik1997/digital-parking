package com.example.digitalparking.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionUuid;

    @Column(unique = true)
    @Size(min = 5, max = 100)
    private String txRef;

    private Double amount;
    private String currency;
    private String status; // pending, success, failed
    private String customerEmail;
    private String customerName;
    private String customerPhone;
    private String orderUuid;
    private String serviceType;

    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime webhookReceivedAt;

    @Column(columnDefinition = "TEXT")
    private String webhookPayload;

    @PrePersist
    protected void onCreate() {
        if (this.transactionUuid == null) {
            this.transactionUuid = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
    }
}