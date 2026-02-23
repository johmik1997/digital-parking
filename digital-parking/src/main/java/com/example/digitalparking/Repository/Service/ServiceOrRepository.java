package com.example.digitalparking.Repository.Service;

import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOrRepository extends JpaRepository<ServiceOrder, Long> {
    Optional<ServiceOrder> findByOrderUuid(String orderUuid);
    List<ServiceOrder> findByStatus(OrderStatus status);
}
