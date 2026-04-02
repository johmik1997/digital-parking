package com.example.digitalparking.ServiceImpl.ServiceImpl;

import com.example.digitalparking.Dto.Request.Service.ServiceOrderRequest;
import com.example.digitalparking.Dto.Response.ClientServiceResponse;
import com.example.digitalparking.Dto.Response.ParkingAvailabilityResponse;
import com.example.digitalparking.Dto.Response.ServiceOrderResponse;
import com.example.digitalparking.Entity.Service.*;
import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Enum.OrderStatus;
import com.example.digitalparking.Repository.Service.ServiceOrderRepository;
import com.example.digitalparking.Repository.Service.ServiceRepository;
import com.example.digitalparking.Repository.TransactionRepository;
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.Service.ClientServiceService;
import com.example.digitalparking.exception.InvalidCredentialsException;
import com.example.digitalparking.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceServiceImpl implements ClientServiceService {

    private static final List<String> PARKING_TIME_SLOTS = List.of(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"
    );
    private static final DateTimeFormatter PARKING_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private static final int ARRIVAL_GRACE_MINUTES = 15;
    private static final int OVERTIME_GRACE_MINUTES = 15;
    private final ServiceRepository serviceRepository;
    private final ServiceOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<ClientServiceResponse> getActiveServicesWithCurrentRates() {

        List<ServiceEntity> activeServices = serviceRepository.findByActiveTrue();

        return activeServices.stream().map(service -> {

            return ClientServiceResponse.builder()
                    .serviceUuid(service.getServiceUUid())
                    .name(service.getName())
                    .type(inferServiceType(service.getName()))
                    .description(service.getDescription())
                    .pricingType(service.getPricingType())
                    .slot(service.getSlot())
                    .active(service.getActive())
                    .currentRate(service.getCurrentRate())
                    .build();

        }).toList();
    }


    @Override
    public ServiceEntity getServiceWithCurrentRate(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        return service;
    }

    @Override
    @Transactional
    public ServiceOrder createOrder(ServiceOrderRequest request, String clientId) {
        log.info("Creating service order for client: {}, service: {}", clientId, request.getServiceUuid());

        // Get client
        UserEntity client = userRepository.findByUserUuid(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        // Get service
        ServiceEntity service = serviceRepository.findByServiceUUid(request.getServiceUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + request.getServiceUuid()));

        if (!service.getActive()) {
            throw new IllegalStateException("Service is not currently active");
        }

        if (isParkingService(service, request)) {
            validateParkingLocationSelection(request);
            validateParkingSlotAvailability(service.getId(), request);
        }

        // Get current rate
        BigDecimal currentRate = service.getCurrentRate();
        if (currentRate == null) {
            throw new IllegalStateException("No active rate found for this service");
        }

        // Validate pricing
        validateOrderPricing(request, service, currentRate);

        // Build order
        String durationStr = request.getDuration();
        Double durationHours = null;
        if (durationStr != null && !durationStr.isBlank()) {
            try {
                durationHours = Double.valueOf(durationStr);
            } catch (NumberFormatException ignored) {
                durationHours = null;
            }
        }

        String addons = null;
        if (request.getAddons() != null && !request.getAddons().isEmpty()) {
            addons = String.join(",", request.getAddons());
        }

        ServiceOrder order = ServiceOrder.builder()
                .service(service)
                .client(client)
                .rateApplied(currentRate)
                .serviceType(blankToNull(resolveServiceType(service, request)))
                .appointmentDate(blankToNull(request.getAppointmentDate()))
                .appointmentTime(blankToNull(request.getAppointmentTime()))
                .vehiclePlate(normalizeVehiclePlate(request.getVehiclePlate()))
                .vehicleType(blankToNull(request.getVehicleType()))
                .washPackage(blankToNull(request.getWashPackage()))
                .parkingDate(blankToNull(request.getParkingDate()))
                .entranceName(resolveEntranceName(request))
                .parkingLevelType(blankToNull(request.getParkingLevelType()))
                .parkingLevelCode(blankToNull(request.getParkingLevelCode()))
                .parkingZone(blankToNull(request.getParkingZone()))
                .selectedSlot(blankToNull(request.getSelectedSlot()))
                .scheduledEntryTime(blankToNull(request.getEntryTime()))
                .entryTime(null)
                .duration(blankToNull(request.getDuration()))
                .visitDate(blankToNull(request.getVisitDate()))
                .ticketType(blankToNull(request.getTicketType()))
                .quantity(request.getQuantity())
                .addons(blankToNull(addons))
                .notes(blankToNull(request.getNotes()))
                .orderDate(blankToNull(request.getOrderDate()))
                .durationHours(durationHours)
                .totalAmount(BigDecimal.valueOf(request.getTotalAmount()))
                .status(OrderStatus.PENDING)
                .build();

        ServiceOrder savedOrder = orderRepository.save(order);
        log.info("Service order created successfully with id: {}", savedOrder.getId());

        return savedOrder;
    }

    private boolean isParkingService(ServiceEntity service, ServiceOrderRequest request) {
        if (request.getServiceType() != null) {
            return "PARKING".equalsIgnoreCase(request.getServiceType());
        }
        return service.getName() != null && service.getName().toLowerCase().contains("parking");
    }

    private String resolveServiceType(ServiceEntity service, ServiceOrderRequest request) {
        if (request.getServiceType() != null && !request.getServiceType().isBlank()) {
            return request.getServiceType();
        }
        return inferServiceType(service.getName());
    }

    private String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String inferServiceType(String serviceName) {
        if (serviceName == null) return null;
        String name = serviceName.toLowerCase();
        if (name.contains("car wash")) return "CAR_WASH";
        if (name.contains("parking")) return "PARKING";
        if (name.contains("meskel") || name.contains("amusement")) return "AMUSEMENT";
        return null;
    }

    private void validateParkingLocationSelection(ServiceOrderRequest request) {
        if (blankToNull(request.getParkingLevelType()) == null) {
            throw new IllegalArgumentException("Parking level type is required");
        }
        if (blankToNull(request.getParkingLevelCode()) == null) {
            throw new IllegalArgumentException("Parking level code is required");
        }
        if (blankToNull(request.getParkingZone()) == null) {
            throw new IllegalArgumentException("Parking zone is required");
        }
        if (blankToNull(request.getSelectedSlot()) == null) {
            throw new IllegalArgumentException("Parking slot is required");
        }
    }

    private String resolveEntranceName(ServiceOrderRequest request) {
        String provided = blankToNull(request.getEntranceName());
        if (provided != null) {
            return provided;
        }

        String levelType = blankToNull(request.getParkingLevelType());
        String levelCode = blankToNull(request.getParkingLevelCode());
        if (levelCode == null) {
            return "Main Entrance";
        }

        if ("BASEMENT".equalsIgnoreCase(levelType) || levelCode.toUpperCase().startsWith("B")) {
            return "Basement Gate";
        }

        if ("GROUND".equalsIgnoreCase(levelType) || "G".equalsIgnoreCase(levelCode)) {
            return "Main Entrance";
        }

        return "Upper Ramp Entrance";
    }

    private void validateParkingSlotAvailability(Long serviceId, ServiceOrderRequest request) {
        String parkingDate = blankToNull(request.getParkingDate());
        String scheduledEntryTime = blankToNull(request.getEntryTime());
        String parkingLevelCode = blankToNull(request.getParkingLevelCode());
        String parkingZone = blankToNull(request.getParkingZone());
        String selectedSlot = blankToNull(request.getSelectedSlot());

        if (parkingDate == null) {
            throw new IllegalArgumentException("Parking date is required");
        }
        if (scheduledEntryTime == null) {
            throw new IllegalArgumentException("Entry time is required");
        }

        LocalDate requestedDate = parseParkingDate(parkingDate);
        LocalDateTime requestedStart = resolveParkingDateTime(requestedDate, scheduledEntryTime);
        int requestedMinutes = resolveRequestedParkingMinutes(request.getDuration());
        LocalDateTime requestedEnd = requestedStart.plusMinutes(requestedMinutes);

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        int capacity = service.getSlot() != null ? service.getSlot() : 0;
        if (capacity <= 0) {
            throw new IllegalStateException("No parking capacity configured for this service");
        }

        List<ParkingReservationWindow> reservationWindows = buildParkingReservationWindows(
                serviceId,
                requestedDate
        );
        long booked = maxBookedDuringWindow(reservationWindows, requestedStart, requestedEnd);

        if (booked >= capacity) {
            throw new IllegalStateException("Selected booking window is fully booked. Please choose another time.");
        }

        if (parkingLevelCode == null || parkingZone == null || selectedSlot == null) {
            return;
        }

        String slotKey = buildParkingSlotKey(parkingLevelCode, parkingZone, selectedSlot);
        long conflictingSlotBookings = reservationWindows.stream()
                .filter(window -> slotKey.equals(window.slotKey()))
                .filter(window -> windowsOverlap(window.start(), window.end(), requestedStart, requestedEnd))
                .count();

        if (conflictingSlotBookings > 0) {
            throw new IllegalStateException("Selected slot is already reserved during this booking window. Please choose another slot.");
        }
    }
    private void validateOrderPricing(ServiceOrderRequest request, ServiceEntity service, BigDecimal currentRate) {
        BigDecimal calculatedTotal;

        if ("HOURLY".equals(service.getPricingType())) {
            if (request.getDuration() == null || request.getDuration().isBlank()) {
                throw new IllegalArgumentException("Duration is required for hourly services");
            }
            calculatedTotal = currentRate.multiply(BigDecimal.valueOf(Long.parseLong(request.getDuration())));
        } else {
            int qty = request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1;
            calculatedTotal = currentRate.multiply(BigDecimal.valueOf(qty));
        }

        // Allow small rounding differences (1 cent)
        if (calculatedTotal.subtract(BigDecimal.valueOf(request.getTotalAmount())).abs().compareTo(BigDecimal.valueOf(0.01)) > 50) {
            log.error("Price mismatch. Calculated: {}, Provided: {}", calculatedTotal, request.getTotalAmount());
            throw new IllegalArgumentException("Invalid total amount. Please refresh and try again.");
        }
    }

    @Override
    public List<ServiceOrder> getClientOrders(String clientId) {
        return orderRepository.findByClientUuidOrderByCreatedAtDesc(clientId);
    }

    @Override
    public List<ServiceOrderResponse> getClientOrderResponses(String clientId) {
        List<ServiceOrder> orders = orderRepository.findByClientUuidOrderByCreatedAtDesc(clientId);

        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceOrder getOrderDetails(String orderId, String clientId) {
        ServiceOrder order = orderRepository.findByOrderUuid(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getClient().getUserUuid().equals(clientId)) {
            throw new InvalidCredentialsException("You don't have permission to view this order");
        }

        return order;
    }

    @Override
    public ServiceOrderResponse getOrderDetailsResponse(String orderId, String clientId) {
        ServiceOrder order = getOrderDetails(orderId, clientId);
        return mapToResponse(order);
    }

    @Override
    public List<ParkingAvailabilityResponse> getParkingAvailability(String serviceUuid, String parkingDate, String duration) {
        ServiceEntity service = serviceRepository.findByServiceUUid(serviceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceUuid));

        int capacity = service.getSlot() != null ? service.getSlot() : 0;
        LocalDate requestedDate = parseParkingDate(parkingDate);
        int requestedMinutes = resolveRequestedParkingMinutes(duration);
        List<ParkingReservationWindow> reservationWindows = buildParkingReservationWindows(service.getId(), requestedDate);

        return PARKING_TIME_SLOTS.stream()
                .map(time -> {
                    LocalDateTime requestedStart = resolveParkingDateTime(requestedDate, time);
                    LocalDateTime requestedEnd = requestedStart.plusMinutes(requestedMinutes);
                    long booked = maxBookedDuringWindow(reservationWindows, requestedStart, requestedEnd);
                    int remaining = Math.max(0, capacity - (int) booked);
                    boolean available = capacity > 0 && booked < capacity;
                    List<String> reservedSlotKeys = reservationWindows.stream()
                            .filter(window -> window.slotKey() != null)
                            .filter(window -> windowsOverlap(window.start(), window.end(), requestedStart, requestedEnd))
                            .map(ParkingReservationWindow::slotKey)
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toCollection(LinkedHashSet::new),
                                    List::copyOf
                            ));
                    return ParkingAvailabilityResponse.builder()
                            .entryTime(time)
                            .capacity(capacity)
                            .booked(booked)
                            .remaining(remaining)
                            .available(available)
                            .reservedSlotKeys(reservedSlotKeys)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceOrder cancelOrder(String orderId, String clientId) {
        ServiceOrder order = getOrderDetails(orderId, clientId);

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public ServiceOrder completeOrder(String orderId) {
        ServiceOrder order = orderRepository.findByOrderUuid(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public ServiceOrderResponse acceptParkingArrival(String vehiclePlate) {
        if (vehiclePlate == null || vehiclePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle plate is required");
        }

        ServiceOrder order = findCashierParkingReservation(vehiclePlate);

        if (order.getServiceType() == null || !"PARKING".equalsIgnoreCase(order.getServiceType())) {
            throw new IllegalStateException("This appointment is not a parking service");
        }

        if (order.getParkingDate() != null && !order.getParkingDate().isBlank()) {
            String today = LocalDate.now().toString();
            if (!order.getParkingDate().equals(today)) {
                throw new IllegalStateException("This appointment is not scheduled for today");
            }
        }

        if (blankToNull(order.getScheduledEntryTime()) == null) {
            order.setScheduledEntryTime(resolveScheduledEntryTime(order));
        }
        order.setEntryTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        order.setStatus(OrderStatus.PROCESSING);
        order.setCompletedAt(null);
        ServiceOrder saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    public ServiceOrderResponse findParkingAppointment(String vehiclePlate) {
        ServiceOrder order = findCashierParkingReservation(vehiclePlate);
        return mapToResponse(order);
    }

    @Override
    public List<ServiceOrderResponse> getActiveOrders(String serviceType) {
        String type = serviceType != null ? serviceType.trim().toUpperCase() : null;
        List<ServiceOrder> orders;
        if (type == null || type.isBlank()) {
            orders = orderRepository.findByStatusAndServiceTypeOrderByCreatedAtDesc(OrderStatus.PROCESSING, "PARKING");
        } else {
            orders = orderRepository.findByStatusAndServiceTypeOrderByCreatedAtDesc(OrderStatus.PROCESSING, type);
        }
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ServiceOrderResponse> getCashierReservations(String parkingDate) {
        String targetDate = blankToNull(parkingDate);
        if (targetDate == null) {
            targetDate = LocalDate.now().toString();
        }

        return orderRepository.findCashierReservations(
                        "PARKING",
                        targetDate,
                        List.of(OrderStatus.PENDING, OrderStatus.COMPLETED)
                ).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceOrderResponse> getCompletedOrders(String serviceType) {
        String type = serviceType != null ? serviceType.trim().toUpperCase() : "PARKING";
        return orderRepository
                .findTop20ByStatusAndServiceTypeAndEntryTimeIsNotNullOrderByCompletedAtDesc(OrderStatus.COMPLETED, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceOrderResponse checkoutParkingOrder(String orderUuid, String paymentMethod) {
        ServiceOrder order = orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderUuid));

        if (!"PARKING".equalsIgnoreCase(blankToNull(order.getServiceType()))) {
            throw new IllegalStateException("This order is not a parking service");
        }

        if (order.getStatus() != OrderStatus.PROCESSING || blankToNull(order.getEntryTime()) == null) {
            throw new IllegalStateException("This vehicle has not been checked in yet");
        }

        ParkingCashierSnapshot snapshot = buildParkingCashierSnapshot(order, LocalDateTime.now());
        String normalizedPaymentMethod = blankToNull(paymentMethod);
        if (snapshot.amountDueNow().compareTo(BigDecimal.ZERO) > 0 && normalizedPaymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required when there is an amount due");
        }

        order.setTotalAmount(snapshot.finalAmount());
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        order.setNotes(buildCashierCheckoutNote(order.getNotes(), snapshot, normalizedPaymentMethod));

        ServiceOrder saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    private ServiceOrder findCashierParkingReservation(String vehiclePlate) {
        if (vehiclePlate == null || vehiclePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle plate is required");
        }

        String normalizedPlate = vehiclePlate.trim().toUpperCase();
        ServiceOrder order = orderRepository
                .findLatestCashierReservationByVehiclePlate(
                        normalizedPlate,
                        List.of(OrderStatus.PENDING, OrderStatus.COMPLETED)
                )
                .orElseThrow(() -> new ResourceNotFoundException("No reserved appointment found for this plate"));

        if (order.getServiceType() == null || !"PARKING".equalsIgnoreCase(order.getServiceType())) {
            throw new IllegalStateException("This appointment is not a parking service");
        }

        if (order.getParkingDate() != null && !order.getParkingDate().isBlank()) {
            String today = LocalDate.now().toString();
            if (!order.getParkingDate().equals(today)) {
                throw new IllegalStateException("This appointment is not scheduled for today");
            }
        }

        return order;
    }

    private ServiceOrderResponse mapToResponse(ServiceOrder order) {
        String parkingLocationDisplay = buildParkingLocationDisplay(order);
        String googleMapsUrl = buildGoogleMapsUrl(order);
        String navigationInstructions = buildNavigationInstructions(order, parkingLocationDisplay);
        ParkingCashierSnapshot parkingSnapshot = buildParkingCashierSnapshot(order, LocalDateTime.now());
        String clientFirstName = order.getClient().getFirstName() == null ? "" : order.getClient().getFirstName().trim();
        String clientFatherName = order.getClient().getFatherName() == null ? "" : order.getClient().getFatherName().trim();
        String clientName = (clientFirstName + " " + clientFatherName).trim();

        return ServiceOrderResponse.builder()
                .id(order.getId())
                .orderUuid(order.getOrderUuid())
                .serviceName(order.getService().getName())
                .serviceDescription(order.getService().getDescription())
                .serviceType(order.getServiceType())
                .pricingType(order.getService().getPricingType())
                .duration(order.getDuration())
                .durationHours(order.getDurationHours())
                .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : null)
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .rateApplied(order.getRateApplied() != null ? order.getRateApplied().doubleValue() : null)
                .appointmentDate(blankToNull(order.getAppointmentDate()))
                .appointmentTime(blankToNull(order.getAppointmentTime()))
                .vehiclePlate(blankToNull(order.getVehiclePlate()))
                .vehicleType(blankToNull(order.getVehicleType()))
                .washPackage(blankToNull(order.getWashPackage()))
                .parkingDate(blankToNull(order.getParkingDate()))
                .entranceName(blankToNull(order.getEntranceName()))
                .parkingLevelType(blankToNull(order.getParkingLevelType()))
                .parkingLevelCode(blankToNull(order.getParkingLevelCode()))
                .parkingZone(blankToNull(order.getParkingZone()))
                .selectedSlot(blankToNull(order.getSelectedSlot()))
                .parkingLocationDisplay(parkingLocationDisplay)
                .googleMapsUrl(googleMapsUrl)
                .navigationInstructions(navigationInstructions)
                .scheduledEntryTime(resolveScheduledEntryTime(order))
                .entryTime(blankToNull(order.getEntryTime()))
                .completedAt(order.getCompletedAt())
                .visitDate(blankToNull(order.getVisitDate()))
                .ticketType(blankToNull(order.getTicketType()))
                .quantity(order.getQuantity())
                .addons(blankToNull(order.getAddons()))
                .notes(blankToNull(order.getNotes()))
                .orderDate(blankToNull(order.getOrderDate()))
                .clientName(clientName.isBlank() ? null : clientName)
                .clientEmail(order.getClient().getEmail())
                .bookedAmount(toDouble(parkingSnapshot.bookedAmount()))
                .prepaidAmount(toDouble(parkingSnapshot.prepaidAmount()))
                .overtimeAmount(toDouble(parkingSnapshot.overtimeAmount()))
                .amountDueNow(toDouble(parkingSnapshot.amountDueNow()))
                .bookedMinutes(parkingSnapshot.bookedMinutes())
                .elapsedMinutes(parkingSnapshot.elapsedMinutes())
                .lateMinutes(parkingSnapshot.lateMinutes())
                .overtimeMinutes(parkingSnapshot.overtimeMinutes())
                .prepaid(parkingSnapshot.prepaid())
                .arrivalWindowStatus(parkingSnapshot.arrivalWindowStatus())
                .paymentStatusLabel(parkingSnapshot.paymentStatusLabel())
                .plannedExitTime(parkingSnapshot.plannedExitTime())
                .build();
    }

    private ParkingCashierSnapshot buildParkingCashierSnapshot(ServiceOrder order, LocalDateTime now) {
        if (!"PARKING".equalsIgnoreCase(blankToNull(order.getServiceType()))) {
            return ParkingCashierSnapshot.empty();
        }

        LocalDateTime evaluationTime = now;
        if (blankToNull(order.getEntryTime()) != null
                && order.getStatus() == OrderStatus.COMPLETED
                && order.getCompletedAt() != null) {
            evaluationTime = order.getCompletedAt();
        }

        int bookedMinutes = resolveBookedMinutes(order);
        BigDecimal bookedAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal prepaidAmount = resolvePrepaidAmount(order);
        boolean prepaid = prepaidAmount.compareTo(BigDecimal.ZERO) > 0;

        LocalDateTime scheduledStart = resolveScheduledDateTime(order);
        LocalDateTime actualEntry = resolveActualEntryDateTime(order);
        LocalDateTime plannedExit = resolvePlannedExitTime(scheduledStart, actualEntry, bookedMinutes);

        int lateMinutes = 0;
        String arrivalWindowStatus = "UNSCHEDULED";
        if (scheduledStart != null) {
            LocalDateTime referenceArrival = actualEntry != null ? actualEntry : evaluationTime;
            long deltaMinutes = Duration.between(scheduledStart, referenceArrival).toMinutes();
            lateMinutes = deltaMinutes > ARRIVAL_GRACE_MINUTES ? safeInt(deltaMinutes) : 0;
            if (deltaMinutes < -ARRIVAL_GRACE_MINUTES) {
                arrivalWindowStatus = "EARLY";
            } else if (deltaMinutes <= ARRIVAL_GRACE_MINUTES) {
                arrivalWindowStatus = "ON_TIME";
            } else if (deltaMinutes <= 60) {
                arrivalWindowStatus = "LATE";
            } else {
                arrivalWindowStatus = "VERY_LATE";
            }
        }

        int elapsedMinutes = 0;
        if (actualEntry != null) {
            elapsedMinutes = Math.max(0, safeInt(Duration.between(actualEntry, evaluationTime).toMinutes()));
        }

        int overtimeMinutes = 0;
        BigDecimal overtimeAmount = BigDecimal.ZERO;
        if (actualEntry != null && plannedExit != null) {
            long minutesPastPlannedExit = Duration.between(plannedExit, evaluationTime).toMinutes();
            if (minutesPastPlannedExit > OVERTIME_GRACE_MINUTES) {
                overtimeMinutes = safeInt(minutesPastPlannedExit - OVERTIME_GRACE_MINUTES);
                overtimeAmount = calculateOvertimeAmount(order, overtimeMinutes);
            }
        }

        BigDecimal baseAmountDue = bookedAmount.subtract(prepaidAmount).max(BigDecimal.ZERO);
        BigDecimal amountDueNow = baseAmountDue.add(overtimeAmount).max(BigDecimal.ZERO);
        BigDecimal finalAmount = bookedAmount.add(overtimeAmount);

        String paymentStatusLabel;
        if (amountDueNow.compareTo(BigDecimal.ZERO) == 0 && prepaid) {
            paymentStatusLabel = overtimeAmount.compareTo(BigDecimal.ZERO) > 0 ? "OVERTIME PAID ONLINE" : "PREPAID";
        } else if (amountDueNow.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatusLabel = "NO BALANCE";
        } else if (overtimeAmount.compareTo(BigDecimal.ZERO) > 0 && prepaid) {
            paymentStatusLabel = "EXTRA TIME DUE";
        } else if (overtimeAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatusLabel = "PARKING + EXTRA TIME";
        } else {
            paymentStatusLabel = "PAY AT EXIT";
        }

        return new ParkingCashierSnapshot(
                bookedAmount,
                prepaidAmount,
                overtimeAmount,
                amountDueNow,
                finalAmount,
                bookedMinutes,
                elapsedMinutes,
                lateMinutes,
                overtimeMinutes,
                prepaid,
                arrivalWindowStatus,
                paymentStatusLabel,
                plannedExit != null ? plannedExit.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
        );
    }

    private BigDecimal resolvePrepaidAmount(ServiceOrder order) {
        if (order == null || blankToNull(order.getOrderUuid()) == null) {
            return BigDecimal.ZERO;
        }

        Double amount = transactionRepository.sumAmountByOrderUuidAndStatus(order.getOrderUuid(), "success");
        BigDecimal prepaidAmount = amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
        if (order.getTotalAmount() == null) {
            return prepaidAmount.max(BigDecimal.ZERO);
        }
        return prepaidAmount.max(BigDecimal.ZERO).min(order.getTotalAmount().max(BigDecimal.ZERO));
    }

    private int resolveBookedMinutes(ServiceOrder order) {
        if (order == null) {
            return 0;
        }

        return resolveDurationMinutes(order.getDuration(), order.getDurationHours());
    }

    private int resolveRequestedParkingMinutes(String duration) {
        return normalizeBookingMinutes(resolveDurationMinutes(duration, null));
    }

    private int resolveDurationMinutes(String durationValue, Double durationHours) {
        if (durationHours != null && durationHours > 0) {
            return Math.max(0, (int) Math.round(durationHours * 60));
        }

        String normalizedValue = blankToNull(durationValue);
        if (normalizedValue == null) {
            return 0;
        }

        String normalized = normalizedValue.trim().toLowerCase()
                .replace("hours", "")
                .replace("hour", "")
                .replace("hrs", "")
                .replace("hr", "")
                .replace("h", "")
                .trim();

        try {
            double hours = Double.parseDouble(normalized);
            return Math.max(0, (int) Math.round(hours * 60));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private int normalizeBookingMinutes(int durationMinutes) {
        return durationMinutes > 0 ? durationMinutes : 60;
    }

    private LocalDateTime resolveScheduledDateTime(ServiceOrder order) {
        String parkingDate = blankToNull(order.getParkingDate());
        String scheduledEntryTime = resolveScheduledEntryTime(order);
        if (parkingDate == null || scheduledEntryTime == null) {
            return null;
        }

        try {
            return LocalDateTime.of(
                    LocalDate.parse(parkingDate),
                    LocalTime.parse(scheduledEntryTime, PARKING_TIME_FORMATTER)
            );
        } catch (Exception ignored) {
            return null;
        }
    }

    private LocalDateTime resolveActualEntryDateTime(ServiceOrder order) {
        String entryTime = blankToNull(order.getEntryTime());
        if (entryTime == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(entryTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception ignored) {
            return null;
        }
    }

    private LocalDateTime resolvePlannedExitTime(LocalDateTime scheduledStart,
                                                 LocalDateTime actualEntry,
                                                 int bookedMinutes) {
        if (bookedMinutes <= 0) {
            return null;
        }

        if (actualEntry != null) {
            return actualEntry.plusMinutes(bookedMinutes);
        }

        if (scheduledStart != null) {
            return scheduledStart.plusMinutes(bookedMinutes);
        }

        return null;
    }

    private LocalDate parseParkingDate(String parkingDate) {
        try {
            return LocalDate.parse(parkingDate);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid parking date");
        }
    }

    private LocalDateTime resolveParkingDateTime(LocalDate parkingDate, String entryTime) {
        try {
            return LocalDateTime.of(parkingDate, LocalTime.parse(entryTime, PARKING_TIME_FORMATTER));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid parking entry time");
        }
    }

    private List<ParkingReservationWindow> buildParkingReservationWindows(Long serviceId, LocalDate parkingDate) {
        LocalDate fromParkingDate = parkingDate.minusDays(1);
        LocalDate toParkingDate = parkingDate.plusDays(1);

        return orderRepository.findParkingOrdersForAvailability(
                        serviceId,
                        fromParkingDate.toString(),
                        toParkingDate.toString(),
                        OrderStatus.CANCELLED
                ).stream()
                .map(this::buildParkingReservationWindow)
                .filter(Objects::nonNull)
                .toList();
    }

    private ParkingReservationWindow buildParkingReservationWindow(ServiceOrder order) {
        if (order == null) {
            return null;
        }

        LocalDateTime scheduledStart = resolveScheduledDateTime(order);
        LocalDateTime actualEntry = resolveActualEntryDateTime(order);
        LocalDateTime start = actualEntry != null ? actualEntry : scheduledStart;
        if (start == null) {
            return null;
        }

        int bookedMinutes = normalizeBookingMinutes(resolveBookedMinutes(order));
        LocalDateTime plannedEnd = actualEntry != null
                ? actualEntry.plusMinutes(bookedMinutes)
                : start.plusMinutes(bookedMinutes);

        LocalDateTime end = plannedEnd;
        if (actualEntry != null && order.getCompletedAt() != null && order.getCompletedAt().isAfter(start)) {
            end = order.getCompletedAt();
        } else if (actualEntry != null
                && order.getStatus() == OrderStatus.PROCESSING
                && LocalDateTime.now().isAfter(plannedEnd)) {
            end = LocalDateTime.now();
        }

        if (end == null || !end.isAfter(start)) {
            end = start.plusMinutes(bookedMinutes);
        }

        String levelCode = blankToNull(order.getParkingLevelCode());
        String zone = blankToNull(order.getParkingZone());
        String slot = blankToNull(order.getSelectedSlot());
        String slotKey = levelCode != null && zone != null && slot != null
                ? buildParkingSlotKey(levelCode, zone, slot)
                : null;

        return new ParkingReservationWindow(start, end, slotKey);
    }

    private long maxBookedDuringWindow(List<ParkingReservationWindow> reservationWindows,
                                       LocalDateTime requestedStart,
                                       LocalDateTime requestedEnd) {
        if (requestedStart == null || requestedEnd == null || !requestedEnd.isAfter(requestedStart)) {
            return 0;
        }

        List<ParkingOccupancyEvent> events = reservationWindows.stream()
                .filter(window -> windowsOverlap(window.start(), window.end(), requestedStart, requestedEnd))
                .flatMap(window -> {
                    LocalDateTime overlapStart = laterOf(window.start(), requestedStart);
                    LocalDateTime overlapEnd = earlierOf(window.end(), requestedEnd);
                    return List.of(
                            new ParkingOccupancyEvent(overlapStart, 1),
                            new ParkingOccupancyEvent(overlapEnd, -1)
                    ).stream();
                })
                .sorted(Comparator
                        .comparing(ParkingOccupancyEvent::time)
                        .thenComparingInt(ParkingOccupancyEvent::delta))
                .toList();

        long active = 0;
        long max = 0;
        for (ParkingOccupancyEvent event : events) {
            active += event.delta();
            if (active > max) {
                max = active;
            }
        }

        return max;
    }

    private boolean windowsOverlap(LocalDateTime startA,
                                   LocalDateTime endA,
                                   LocalDateTime startB,
                                   LocalDateTime endB) {
        return startA != null
                && endA != null
                && startB != null
                && endB != null
                && startA.isBefore(endB)
                && startB.isBefore(endA);
    }

    private LocalDateTime laterOf(LocalDateTime first, LocalDateTime second) {
        return first.isAfter(second) ? first : second;
    }

    private LocalDateTime earlierOf(LocalDateTime first, LocalDateTime second) {
        return first.isBefore(second) ? first : second;
    }

    private String normalizeVehiclePlate(String vehiclePlate) {
        String normalized = blankToNull(vehiclePlate);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private BigDecimal calculateOvertimeAmount(ServiceOrder order, int overtimeMinutes) {
        if (order == null || overtimeMinutes <= 0 || order.getRateApplied() == null) {
            return BigDecimal.ZERO;
        }

        int overtimeHours = (int) Math.ceil(overtimeMinutes / 60.0);
        return order.getRateApplied()
                .multiply(BigDecimal.valueOf(overtimeHours))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String buildCashierCheckoutNote(String existingNotes,
                                            ParkingCashierSnapshot snapshot,
                                            String paymentMethod) {
        List<String> lines = new ArrayList<>();
        String trimmedNotes = blankToNull(existingNotes);
        if (trimmedNotes != null) {
            lines.add(trimmedNotes);
        }

        String checkoutLine = "Cashier checkout: "
                + (paymentMethod != null ? paymentMethod.toUpperCase() : "NO_CHARGE")
                + " | due now ETB " + formatMoney(snapshot.amountDueNow())
                + " | prepaid ETB " + formatMoney(snapshot.prepaidAmount())
                + " | overtime ETB " + formatMoney(snapshot.overtimeAmount());
        lines.add(checkoutLine);

        if (snapshot.lateMinutes() > 0) {
            lines.add("Late arrival: " + snapshot.lateMinutes() + " min");
        }

        if (snapshot.overtimeMinutes() > 0) {
            lines.add("Overtime: " + snapshot.overtimeMinutes() + " min after grace");
        }

        return String.join(" | ", lines);
    }

    private String formatMoney(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private Double toDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }

    private int safeInt(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    private String buildParkingLocationDisplay(ServiceOrder order) {
        if (!"PARKING".equalsIgnoreCase(blankToNull(order.getServiceType()))) {
            return null;
        }

        String levelType = normalizeLevelType(order.getParkingLevelType());
        String levelCode = blankToNull(order.getParkingLevelCode());
        String zone = blankToNull(order.getParkingZone());
        String slot = blankToNull(order.getSelectedSlot());

        StringBuilder display = new StringBuilder();
        if (levelType != null || levelCode != null) {
            display.append(levelLabel(levelType, levelCode));
        }
        if (zone != null) {
            if (!display.isEmpty()) display.append(" / ");
            display.append("Zone ").append(zone.toUpperCase());
        }
        if (slot != null) {
            if (!display.isEmpty()) display.append(" / ");
            display.append("Slot ").append(slot.toUpperCase());
        }

        return display.isEmpty() ? null : display.toString();
    }

    private String resolveScheduledEntryTime(ServiceOrder order) {
        return resolveScheduledEntryTime(blankToNull(order.getScheduledEntryTime()), blankToNull(order.getEntryTime()));
    }

    private String resolveScheduledEntryTime(String scheduledEntryTime, String fallbackEntryTime) {
        String scheduled = blankToNull(scheduledEntryTime);
        if (scheduled != null) {
            return scheduled;
        }

        String fallback = blankToNull(fallbackEntryTime);
        if (fallback != null && PARKING_TIME_SLOTS.contains(fallback)) {
            return fallback;
        }

        return null;
    }

    private String buildParkingSlotKey(String levelCode, String zone, String slot) {
        return String.join("|",
                levelCode.toUpperCase(),
                zone.toUpperCase(),
                slot.toUpperCase()
        );
    }

    private String buildGoogleMapsUrl(ServiceOrder order) {
        if (!"PARKING".equalsIgnoreCase(blankToNull(order.getServiceType()))) {
            return null;
        }

        String destination = "Meskel Square Parking, Addis Ababa";
        String entrance = blankToNull(order.getEntranceName());
        if (entrance != null) {
            destination = "Meskel Square Parking " + entrance + ", Addis Ababa";
        }

        return "https://www.google.com/maps/dir/?api=1&destination="
                + URLEncoder.encode(destination, StandardCharsets.UTF_8)
                + "&travelmode=driving";
    }

    private String buildNavigationInstructions(ServiceOrder order, String parkingLocationDisplay) {
        if (!"PARKING".equalsIgnoreCase(blankToNull(order.getServiceType()))) {
            return null;
        }

        String entrance = blankToNull(order.getEntranceName());
        if (parkingLocationDisplay == null && entrance == null) {
            return null;
        }

        if (entrance != null && parkingLocationDisplay != null) {
            return "Navigate to " + entrance + ", then follow signs to " + parkingLocationDisplay + ".";
        }

        if (parkingLocationDisplay != null) {
            return "Follow internal parking signs to " + parkingLocationDisplay + ".";
        }

        return "Navigate to " + entrance + ".";
    }

    private String normalizeLevelType(String levelType) {
        String value = blankToNull(levelType);
        if (value == null) return null;
        return value.toUpperCase();
    }

    private String levelLabel(String levelType, String levelCode) {
        String code = levelCode == null ? "" : levelCode.toUpperCase();

        if ("BASEMENT".equalsIgnoreCase(levelType) || code.startsWith("B")) {
            return "Basement " + code;
        }
        if ("GROUND".equalsIgnoreCase(levelType) || "G".equalsIgnoreCase(code)) {
            return "Ground Floor";
        }
        if ("FLOOR".equalsIgnoreCase(levelType) || code.startsWith("F")) {
            return "Floor " + code.replaceFirst("^F", "");
        }
        return code;
    }

    private record ParkingCashierSnapshot(
            BigDecimal bookedAmount,
            BigDecimal prepaidAmount,
            BigDecimal overtimeAmount,
            BigDecimal amountDueNow,
            BigDecimal finalAmount,
            Integer bookedMinutes,
            Integer elapsedMinutes,
            Integer lateMinutes,
            Integer overtimeMinutes,
            Boolean prepaid,
            String arrivalWindowStatus,
            String paymentStatusLabel,
            String plannedExitTime
    ) {
        private static ParkingCashierSnapshot empty() {
            return new ParkingCashierSnapshot(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0,
                    0,
                    0,
                    0,
                    false,
                    null,
                    null,
                    null
            );
        }
    }

    private record ParkingReservationWindow(
            LocalDateTime start,
            LocalDateTime end,
            String slotKey
    ) {
    }

    private record ParkingOccupancyEvent(
            LocalDateTime time,
            int delta
    ) {
    }
}
