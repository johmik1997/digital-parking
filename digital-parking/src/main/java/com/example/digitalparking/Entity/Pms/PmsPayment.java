package com.example.digitalparking.Entity.Pms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pms_payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plateNo;

    private String entryTime;

    private String exitTime;

    private String cashierId;

    private String paymentMethod;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
