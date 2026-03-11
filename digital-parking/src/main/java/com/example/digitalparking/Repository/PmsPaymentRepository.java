package com.example.digitalparking.Repository;

import com.example.digitalparking.Entity.Pms.PmsPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PmsPaymentRepository extends JpaRepository<PmsPayment, Long> {
}
