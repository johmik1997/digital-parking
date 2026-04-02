package com.example.digitalparking.Repository;

import com.example.digitalparking.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTxRef(String txRef);

    @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.orderUuid = :orderUuid and lower(t.status) = lower(:status)")
    Double sumAmountByOrderUuidAndStatus(@Param("orderUuid") String orderUuid, @Param("status") String status);
}
