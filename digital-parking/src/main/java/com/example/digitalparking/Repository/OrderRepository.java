package com.example.digitalparking.Repository;

import com.example.digitalparking.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Order findByOrderUuid(String orderUuid);
}