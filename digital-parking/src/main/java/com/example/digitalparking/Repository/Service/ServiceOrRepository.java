package com.example.digitalparking.Repository.Service;

import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOrRepository extends JpaRepository<ServiceOrder, Long> {
    Optional<ServiceOrder> findByOrderUuid(String orderUuid);
    List<ServiceOrder> findByStatus(OrderStatus status);
    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from ServiceOrder o where o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("""
            select function('date', o.createdAt) as day, coalesce(sum(o.totalAmount), 0)
            from ServiceOrder o
            where o.status = :status and o.createdAt >= :start
            group by function('date', o.createdAt)
            order by day
            """)
    List<Object[]> sumDailyTotalsSince(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start
    );

    @Query("""
            select o.serviceType, count(o)
            from ServiceOrder o
            where o.serviceType is not null
            group by o.serviceType
            order by count(o) desc
            """)
    List<Object[]> countByServiceType();

    @Query("""
            select o.serviceType, coalesce(sum(o.totalAmount), 0)
            from ServiceOrder o
            where o.status = :status and o.serviceType is not null
            group by o.serviceType
            order by sum(o.totalAmount) desc
            """)
    List<Object[]> sumTotalAmountByServiceType(@Param("status") OrderStatus status);

    @Query("""
            select function('date_part', 'hour', o.createdAt) as hour, count(o)
            from ServiceOrder o
            where o.createdAt is not null
            group by function('date_part', 'hour', o.createdAt)
            order by count(o) desc
            """)
    List<Object[]> countByHourOfDay();

    long countByStatusAndServiceType(OrderStatus status, String serviceType);

    List<ServiceOrder> findTop5ByStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);

    List<ServiceOrder> findTop5ByOrderByCreatedAtDesc();

    long countByClient_UserUuid(String userUuid);

    long countByClient_UserUuidAndStatus(String userUuid, OrderStatus status);

    List<ServiceOrder> findTop5ByClient_UserUuidOrderByCreatedAtDesc(String userUuid);

    Optional<ServiceOrder> findTopByClient_UserUuidAndServiceTypeAndStatusInOrderByCreatedAtDesc(
            String userUuid,
            String serviceType,
            List<OrderStatus> statuses
    );

    Optional<ServiceOrder> findTopByClient_UserUuidAndServiceTypeOrderByCreatedAtDesc(
            String userUuid,
            String serviceType
    );

    @Query("select coalesce(sum(o.totalAmount), 0) from ServiceOrder o where o.client.userUuid = :userUuid and o.status = :status")
    BigDecimal sumTotalAmountByStatusForClient(
            @Param("userUuid") String userUuid,
            @Param("status") OrderStatus status
    );

    @Query("""
            select function('date', o.createdAt) as day, coalesce(sum(o.totalAmount), 0)
            from ServiceOrder o
            where o.status = :status and o.client.userUuid = :userUuid and o.createdAt >= :start
            group by function('date', o.createdAt)
            order by day
            """)
    List<Object[]> sumDailyTotalsSinceForClient(
            @Param("userUuid") String userUuid,
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start
    );

    @Query("""
            select o.serviceType, count(o)
            from ServiceOrder o
            where o.client.userUuid = :userUuid and o.serviceType is not null
            group by o.serviceType
            order by count(o) desc
            """)
    List<Object[]> countByServiceTypeForClient(@Param("userUuid") String userUuid);

    @Query("""
            select o.serviceType, coalesce(sum(o.totalAmount), 0)
            from ServiceOrder o
            where o.status = :status and o.client.userUuid = :userUuid and o.serviceType is not null
            group by o.serviceType
            order by sum(o.totalAmount) desc
            """)
    List<Object[]> sumTotalAmountByServiceTypeForClient(
            @Param("userUuid") String userUuid,
            @Param("status") OrderStatus status
    );
}
