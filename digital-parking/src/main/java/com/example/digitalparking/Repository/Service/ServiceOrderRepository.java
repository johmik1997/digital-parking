package com.example.digitalparking.Repository.Service;

import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    // ✅ Get all orders for a client using userUuid
    @Query("SELECT o FROM ServiceOrder o WHERE o.client.userUuid = :userUuid ORDER BY o.createdAt DESC")
    List<ServiceOrder> findByClientUuidOrderByCreatedAtDesc(@Param("userUuid") String userUuid);

    // ✅ Get order by orderUuid
    Optional<ServiceOrder> findByOrderUuid(String orderUuid);

    // ✅ Get orders for a client with a specific status
    @Query("SELECT o FROM ServiceOrder o WHERE o.client.userUuid = :userUuid AND o.status = :status ORDER BY o.createdAt DESC")
    List<ServiceOrder> findByClientUuidAndStatus(@Param("userUuid") String userUuid,
                                                 @Param("status") OrderStatus status);

    // ✅ Get orders by service id
    @Query("SELECT o FROM ServiceOrder o WHERE o.service.id = :serviceId ORDER BY o.createdAt DESC")
    List<ServiceOrder> findByServiceId(@Param("serviceId") Long serviceId);

    // ✅ Get order by orderUuid and clientUuid
    @Query("SELECT o FROM ServiceOrder o WHERE o.orderUuid = :orderUuid AND o.client.userUuid = :userUuid")
    Optional<ServiceOrder> findByOrderUuidAndClientUuid(@Param("orderUuid") String orderUuid,
                                                        @Param("userUuid") String userUuid);
}
