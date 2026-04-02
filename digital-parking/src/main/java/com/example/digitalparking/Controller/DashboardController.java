package com.example.digitalparking.Controller;

import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Enum.OrderStatus;
import com.example.digitalparking.Enum.Status;
import com.example.digitalparking.Repository.Service.ServiceOrRepository;
import com.example.digitalparking.Repository.Service.ServiceRepository;
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.ServiceImpl.UserServiceImpl.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceOrRepository orderRepository;

    public DashboardController(
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            ServiceOrRepository orderRepository
    ) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsDeletedFalse();
        long activeUserStatus = userRepository.countByUserStatus(Status.ACTIVE);

        long totalServices = serviceRepository.count();
        long activeServices = serviceRepository.countByActiveTrue();

        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);

        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);
        BigDecimal pendingRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.PENDING);

        List<DailyRevenue> last7Days = buildDailyRevenue(OrderStatus.COMPLETED, 7);
        List<ServiceTypeCount> serviceTypeCounts = mapServiceTypes(orderRepository.countByServiceType());
        List<ServiceTypeRevenue> revenueByServiceType = mapServiceTypeRevenue(orderRepository.sumTotalAmountByServiceType(OrderStatus.COMPLETED));

        OccupancySnapshot occupancySnapshot = buildOccupancySnapshot();
        PaymentSuccess paymentSuccess = buildPaymentSuccess();
        List<PeakHour> peakHours = mapPeakHours(orderRepository.countByHourOfDay());
        List<AlertItem> alerts = orderRepository
                .findTop5ByStatusInOrderByCreatedAtDesc(List.of(OrderStatus.FAILED, OrderStatus.CANCELLED))
                .stream()
                .map(this::mapAlert)
                .toList();

        List<RecentOrder> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapRecentOrder)
                .toList();

        DashboardSummary summary = new DashboardSummary(
                new Overview(
                        totalUsers,
                        activeUsers,
                        activeUserStatus,
                        totalServices,
                        activeServices,
                        totalOrders,
                        pendingOrders,
                        completedOrders,
                        totalRevenue,
                        pendingRevenue
                ),
                last7Days,
                serviceTypeCounts,
                revenueByServiceType,
                occupancySnapshot,
                paymentSuccess,
                peakHours,
                alerts,
                recentOrders
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/customer/summary")
    public ResponseEntity<CustomerDashboardSummary> getCustomerSummary() {
        UserDetailsImpl userDetails = getCurrentUser();
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userUuid = userDetails.getUserUuid();

        long totalOrders = orderRepository.countByClient_UserUuid(userUuid);
        long completedOrders = orderRepository.countByClient_UserUuidAndStatus(userUuid, OrderStatus.COMPLETED);
        long pendingOrders = orderRepository.countByClient_UserUuidAndStatus(userUuid, OrderStatus.PENDING);
        long failedOrders = orderRepository.countByClient_UserUuidAndStatus(userUuid, OrderStatus.FAILED);
        long cancelledOrders = orderRepository.countByClient_UserUuidAndStatus(userUuid, OrderStatus.CANCELLED);

        BigDecimal totalSpent = orderRepository.sumTotalAmountByStatusForClient(userUuid, OrderStatus.COMPLETED);
        BigDecimal pendingAmount = orderRepository.sumTotalAmountByStatusForClient(userUuid, OrderStatus.PENDING);

        List<DailyRevenue> last7Days = buildDailyRevenueForClient(userUuid, OrderStatus.COMPLETED, 7);
        List<ServiceTypeCount> serviceTypeCounts = mapServiceTypes(orderRepository.countByServiceTypeForClient(userUuid));
        List<ServiceTypeRevenue> revenueByServiceType =
                mapServiceTypeRevenue(orderRepository.sumTotalAmountByServiceTypeForClient(userUuid, OrderStatus.COMPLETED));

        List<RecentOrder> recentOrders = orderRepository.findTop5ByClient_UserUuidOrderByCreatedAtDesc(userUuid)
                .stream()
                .map(this::mapRecentOrder)
                .toList();

        ParkingReservation latestParkingReservation = resolveLatestParkingReservation(userUuid);

        long totalPayments = completedOrders + failedOrders + cancelledOrders;
        double paymentSuccessRate = totalPayments == 0 ? 0 : (completedOrders * 100.0) / totalPayments;
        PaymentSuccess paymentSuccess = new PaymentSuccess(completedOrders, failedOrders, cancelledOrders, totalPayments, round(paymentSuccessRate));

        CustomerDashboardSummary summary = new CustomerDashboardSummary(
                new CustomerOverview(
                        totalOrders,
                        completedOrders,
                        pendingOrders,
                        failedOrders,
                        cancelledOrders,
                        totalSpent,
                        pendingAmount
                ),
                last7Days,
                serviceTypeCounts,
                revenueByServiceType,
                paymentSuccess,
                recentOrders,
                latestParkingReservation
        );

        return ResponseEntity.ok(summary);
    }

    private List<DailyRevenue> buildDailyRevenue(OrderStatus status, int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDay = today.minusDays(days - 1);
        LocalDateTime startDateTime = startDay.atStartOfDay();

        List<Object[]> rows = orderRepository.sumDailyTotalsSince(status, startDateTime);
        Map<LocalDate, BigDecimal> totals = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate day;
            Object rawDay = row[0];
            if (rawDay instanceof java.sql.Date sqlDate) {
                day = sqlDate.toLocalDate();
            } else if (rawDay instanceof LocalDate localDate) {
                day = localDate;
            } else {
                day = LocalDate.parse(rawDay.toString());
            }
            BigDecimal total = (BigDecimal) row[1];
            totals.put(day, total);
        }

        List<DailyRevenue> series = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = startDay.plusDays(i);
            BigDecimal total = totals.getOrDefault(day, BigDecimal.ZERO);
            series.add(new DailyRevenue(day.toString(), total));
        }
        return series;
    }

    private List<DailyRevenue> buildDailyRevenueForClient(String userUuid, OrderStatus status, int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDay = today.minusDays(days - 1);
        LocalDateTime startDateTime = startDay.atStartOfDay();

        List<Object[]> rows = orderRepository.sumDailyTotalsSinceForClient(userUuid, status, startDateTime);
        Map<LocalDate, BigDecimal> totals = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate day;
            Object rawDay = row[0];
            if (rawDay instanceof java.sql.Date sqlDate) {
                day = sqlDate.toLocalDate();
            } else if (rawDay instanceof LocalDate localDate) {
                day = localDate;
            } else {
                day = LocalDate.parse(rawDay.toString());
            }
            BigDecimal total = (BigDecimal) row[1];
            totals.put(day, total);
        }

        List<DailyRevenue> series = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = startDay.plusDays(i);
            BigDecimal total = totals.getOrDefault(day, BigDecimal.ZERO);
            series.add(new DailyRevenue(day.toString(), total));
        }
        return series;
    }

    private List<ServiceTypeCount> mapServiceTypes(List<Object[]> rows) {
        List<ServiceTypeCount> results = new ArrayList<>();
        for (Object[] row : rows) {
            String type = row[0] != null ? row[0].toString() : "Unknown";
            long count = ((Number) row[1]).longValue();
            results.add(new ServiceTypeCount(type, count));
        }
        return results;
    }

    private List<ServiceTypeRevenue> mapServiceTypeRevenue(List<Object[]> rows) {
        List<ServiceTypeRevenue> results = new ArrayList<>();
        for (Object[] row : rows) {
            String type = row[0] != null ? row[0].toString() : "Unknown";
            BigDecimal total = (BigDecimal) row[1];
            results.add(new ServiceTypeRevenue(type, total));
        }
        return results;
    }

    private List<PeakHour> mapPeakHours(List<Object[]> rows) {
        List<PeakHour> results = new ArrayList<>();
        for (Object[] row : rows) {
            int hour = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            results.add(new PeakHour(String.format("%02d:00", hour), count));
        }
        return results.stream().limit(5).toList();
    }

    private OccupancySnapshot buildOccupancySnapshot() {
        long capacity = serviceRepository.sumActiveSlots() != null ? serviceRepository.sumActiveSlots() : 0L;
        long pending = orderRepository.countByStatusAndServiceType(OrderStatus.PENDING, "PARKING");
        long processing = orderRepository.countByStatusAndServiceType(OrderStatus.PROCESSING, "PARKING");
        long occupied = pending + processing;
        long available = Math.max(capacity - occupied, 0);
        double utilization = capacity == 0 ? 0 : (occupied * 100.0) / capacity;
        return new OccupancySnapshot(capacity, occupied, available, round(utilization));
    }

    private PaymentSuccess buildPaymentSuccess() {
        long completed = orderRepository.countByStatus(OrderStatus.COMPLETED);
        long failed = orderRepository.countByStatus(OrderStatus.FAILED);
        long cancelled = orderRepository.countByStatus(OrderStatus.CANCELLED);
        long total = completed + failed + cancelled;
        double successRate = total == 0 ? 0 : (completed * 100.0) / total;
        return new PaymentSuccess(completed, failed, cancelled, total, round(successRate));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private RecentOrder mapRecentOrder(ServiceOrder order) {
        String serviceName = order.getService() != null ? order.getService().getName() : "Service";
        return new RecentOrder(
                order.getOrderUuid(),
                serviceName,
                order.getServiceType(),
                order.getVehiclePlate(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                order.getParkingDate(),
                resolveScheduledEntryTime(order),
                order.getDuration(),
                order.getEntranceName(),
                order.getParkingLevelType(),
                order.getParkingLevelCode(),
                order.getParkingZone(),
                order.getSelectedSlot()
        );
    }

    private ParkingReservation resolveLatestParkingReservation(String userUuid) {
        ServiceOrder activeParkingOrder = orderRepository
                .findTopByClient_UserUuidAndServiceTypeAndStatusInOrderByCreatedAtDesc(
                        userUuid,
                        "PARKING",
                        List.of(OrderStatus.PENDING, OrderStatus.PROCESSING)
                )
                .orElse(null);

        if (activeParkingOrder != null) {
            return mapParkingReservation(activeParkingOrder);
        }

        return orderRepository
                .findTopByClient_UserUuidAndServiceTypeOrderByCreatedAtDesc(userUuid, "PARKING")
                .map(this::mapParkingReservation)
                .orElse(null);
    }

    private ParkingReservation mapParkingReservation(ServiceOrder order) {
        String serviceName = order.getService() != null ? order.getService().getName() : "Parking";
        return new ParkingReservation(
                order.getOrderUuid(),
                serviceName,
                order.getStatus().name(),
                order.getParkingDate(),
                resolveScheduledEntryTime(order),
                order.getDuration(),
                order.getEntranceName(),
                order.getParkingLevelType(),
                order.getParkingLevelCode(),
                order.getParkingZone(),
                order.getSelectedSlot(),
                order.getVehiclePlate(),
                order.getTotalAmount(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null
        );
    }

    private String resolveScheduledEntryTime(ServiceOrder order) {
        if (order == null) {
            return null;
        }
        String scheduledEntryTime = order.getScheduledEntryTime();
        if (scheduledEntryTime != null && !scheduledEntryTime.isBlank()) {
            return scheduledEntryTime;
        }
        return order.getEntryTime();
    }

    private AlertItem mapAlert(ServiceOrder order) {
        return new AlertItem(
                order.getOrderUuid(),
                order.getStatus().name(),
                order.getServiceType(),
                order.getVehiclePlate(),
                order.getTotalAmount(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null
        );
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl details) {
            return details;
        }
        return null;
    }

    public record DashboardSummary(
            Overview overview,
            List<DailyRevenue> last7DaysRevenue,
            List<ServiceTypeCount> serviceTypeCounts,
            List<ServiceTypeRevenue> revenueByServiceType,
            OccupancySnapshot occupancy,
            PaymentSuccess paymentSuccess,
            List<PeakHour> peakHours,
            List<AlertItem> alerts,
            List<RecentOrder> recentOrders
    ) {}

    public record Overview(
            long totalUsers,
            long activeUsers,
            long activeUserStatus,
            long totalServices,
            long activeServices,
            long totalOrders,
            long pendingOrders,
            long completedOrders,
            BigDecimal totalRevenue,
            BigDecimal pendingRevenue
    ) {}

    public record DailyRevenue(String day, BigDecimal total) {}

    public record ServiceTypeCount(String serviceType, long count) {}

    public record ServiceTypeRevenue(String serviceType, BigDecimal total) {}

    public record OccupancySnapshot(long capacity, long occupied, long available, double utilizationPercent) {}

    public record PaymentSuccess(long completed, long failed, long cancelled, long total, double successRate) {}

    public record PeakHour(String hour, long count) {}

    public record AlertItem(
            String orderUuid,
            String status,
            String serviceType,
            String vehiclePlate,
            BigDecimal totalAmount,
            String createdAt
    ) {}

    public record RecentOrder(
            String orderUuid,
            String serviceName,
            String serviceType,
            String vehiclePlate,
            BigDecimal totalAmount,
            String status,
            String createdAt,
            String parkingDate,
            String scheduledEntryTime,
            String duration,
            String entranceName,
            String parkingLevelType,
            String parkingLevelCode,
            String parkingZone,
            String selectedSlot
    ) {}

    public record ParkingReservation(
            String orderUuid,
            String serviceName,
            String status,
            String parkingDate,
            String scheduledEntryTime,
            String duration,
            String entranceName,
            String parkingLevelType,
            String parkingLevelCode,
            String parkingZone,
            String selectedSlot,
            String vehiclePlate,
            BigDecimal totalAmount,
            String createdAt
    ) {}

    public record CustomerDashboardSummary(
            CustomerOverview overview,
            List<DailyRevenue> last7DaysRevenue,
            List<ServiceTypeCount> serviceTypeCounts,
            List<ServiceTypeRevenue> revenueByServiceType,
            PaymentSuccess paymentSuccess,
            List<RecentOrder> recentOrders,
            ParkingReservation latestParkingReservation
    ) {}

    public record CustomerOverview(
            long totalOrders,
            long completedOrders,
            long pendingOrders,
            long failedOrders,
            long cancelledOrders,
            BigDecimal totalSpent,
            BigDecimal pendingAmount
    ) {}
}
