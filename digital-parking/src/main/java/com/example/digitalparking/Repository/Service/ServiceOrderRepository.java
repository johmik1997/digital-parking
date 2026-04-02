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

    Optional<ServiceOrder> findTopByVehiclePlateAndStatusOrderByCreatedAtDesc(String vehiclePlate, OrderStatus status);

    @Query("SELECT o FROM ServiceOrder o " +
            "WHERE UPPER(o.vehiclePlate) = UPPER(:vehiclePlate) " +
            "AND o.status IN :statuses " +
            "AND o.entryTime IS NULL " +
            "ORDER BY o.createdAt DESC")
    Optional<ServiceOrder> findLatestCashierReservationByVehiclePlate(@Param("vehiclePlate") String vehiclePlate,
                                                                      @Param("statuses") List<OrderStatus> statuses);

    List<ServiceOrder> findByStatusAndServiceTypeOrderByCreatedAtDesc(OrderStatus status, String serviceType);

    List<ServiceOrder> findTop20ByStatusAndServiceTypeAndEntryTimeIsNotNullOrderByCompletedAtDesc(
            OrderStatus status,
            String serviceType
    );

    @Query("SELECT o FROM ServiceOrder o " +
            "WHERE UPPER(o.serviceType) = UPPER(:serviceType) " +
            "AND o.parkingDate = :parkingDate " +
            "AND o.entryTime IS NULL " +
            "AND o.status IN :statuses " +
            "ORDER BY COALESCE(o.scheduledEntryTime, o.entryTime) ASC, o.createdAt DESC")
    List<ServiceOrder> findCashierReservations(@Param("serviceType") String serviceType,
                                               @Param("parkingDate") String parkingDate,
                                               @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM ServiceOrder o " +
            "WHERE o.service.id = :serviceId " +
            "AND o.parkingDate BETWEEN :fromParkingDate AND :toParkingDate " +
            "AND o.status <> :cancelled")
    List<ServiceOrder> findParkingOrdersForAvailability(@Param("serviceId") Long serviceId,
                                                        @Param("fromParkingDate") String fromParkingDate,
                                                        @Param("toParkingDate") String toParkingDate,
                                                        @Param("cancelled") OrderStatus cancelled);

    @Query("SELECT COALESCE(o.scheduledEntryTime, o.entryTime), COUNT(o) FROM ServiceOrder o " +
            "WHERE o.service.id = :serviceId AND o.parkingDate = :parkingDate " +
            "AND COALESCE(o.scheduledEntryTime, o.entryTime) IS NOT NULL " +
            "AND o.status <> :cancelled " +
            "GROUP BY COALESCE(o.scheduledEntryTime, o.entryTime)")
    List<Object[]> countBookingsByTime(@Param("serviceId") Long serviceId,
                                       @Param("parkingDate") String parkingDate,
                                       @Param("cancelled") OrderStatus cancelled);

    @Query("SELECT COUNT(o) FROM ServiceOrder o " +
            "WHERE o.service.id = :serviceId AND o.parkingDate = :parkingDate " +
            "AND COALESCE(o.scheduledEntryTime, o.entryTime) = :entryTime " +
            "AND o.status <> :cancelled")
    long countBookingsForTime(@Param("serviceId") Long serviceId,
                              @Param("parkingDate") String parkingDate,
                              @Param("entryTime") String entryTime,
                              @Param("cancelled") OrderStatus cancelled);

    @Query("SELECT COALESCE(o.scheduledEntryTime, o.entryTime), o.parkingLevelCode, o.parkingZone, o.selectedSlot " +
            "FROM ServiceOrder o " +
            "WHERE o.service.id = :serviceId AND o.parkingDate = :parkingDate " +
            "AND COALESCE(o.scheduledEntryTime, o.entryTime) IS NOT NULL " +
            "AND o.parkingLevelCode IS NOT NULL AND o.parkingZone IS NOT NULL AND o.selectedSlot IS NOT NULL " +
            "AND o.status <> :cancelled")
    List<Object[]> findReservedParkingSlotsByTime(@Param("serviceId") Long serviceId,
                                                  @Param("parkingDate") String parkingDate,
                                                  @Param("cancelled") OrderStatus cancelled);

    @Query("SELECT COUNT(o) FROM ServiceOrder o " +
            "WHERE o.service.id = :serviceId AND o.parkingDate = :parkingDate " +
            "AND COALESCE(o.scheduledEntryTime, o.entryTime) = :entryTime " +
            "AND UPPER(o.parkingLevelCode) = UPPER(:parkingLevelCode) " +
            "AND UPPER(o.parkingZone) = UPPER(:parkingZone) " +
            "AND UPPER(o.selectedSlot) = UPPER(:selectedSlot) " +
            "AND o.status <> :cancelled")
    long countBookingsForParkingSlot(@Param("serviceId") Long serviceId,
                                     @Param("parkingDate") String parkingDate,
                                     @Param("entryTime") String entryTime,
                                     @Param("parkingLevelCode") String parkingLevelCode,
                                     @Param("parkingZone") String parkingZone,
                                     @Param("selectedSlot") String selectedSlot,
                                     @Param("cancelled") OrderStatus cancelled);
}
