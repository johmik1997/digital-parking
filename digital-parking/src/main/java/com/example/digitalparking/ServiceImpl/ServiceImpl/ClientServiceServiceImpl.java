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
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.Service.ClientServiceService;
import com.example.digitalparking.exception.InvalidCredentialsException;
import com.example.digitalparking.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceServiceImpl implements ClientServiceService {

    private static final List<String> PARKING_TIME_SLOTS = List.of(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"
    );
    private final ServiceRepository serviceRepository;
    private final ServiceOrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public List<ClientServiceResponse> getActiveServicesWithCurrentRates() {

        List<ServiceEntity> activeServices = serviceRepository.findByActiveTrue();

        return activeServices.stream().map(service -> {

            return ClientServiceResponse.builder()
                    .serviceUuid(service.getServiceUUid())
                    .name(service.getName())
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
                .vehiclePlate(blankToNull(request.getVehiclePlate()))
                .vehicleType(blankToNull(request.getVehicleType()))
                .washPackage(blankToNull(request.getWashPackage()))
                .parkingDate(blankToNull(request.getParkingDate()))
                .selectedSlot(blankToNull(request.getSelectedSlot()))
                .entryTime(blankToNull(request.getEntryTime()))
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
        if (service.getName() == null) return null;
        String name = service.getName().toLowerCase();
        if (name.contains("car wash")) return "CAR_WASH";
        if (name.contains("parking")) return "PARKING";
        if (name.contains("meskel") || name.contains("amusement")) return "AMUSEMENT";
        return null;
    }

    private String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateParkingSlotAvailability(Long serviceId, ServiceOrderRequest request) {
        if (request.getParkingDate() == null || request.getParkingDate().isBlank()) {
            throw new IllegalArgumentException("Parking date is required");
        }
        if (request.getEntryTime() == null || request.getEntryTime().isBlank()) {
            throw new IllegalArgumentException("Entry time is required");
        }

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        int capacity = service.getSlot() != null ? service.getSlot() : 0;
        if (capacity <= 0) {
            throw new IllegalStateException("No parking capacity configured for this service");
        }

        long booked = orderRepository.countBookingsForTime(
                serviceId,
                request.getParkingDate(),
                request.getEntryTime(),
                OrderStatus.CANCELLED
        );

        if (booked >= capacity) {
            throw new IllegalStateException("Selected time is fully booked. Please choose another time.");
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
    public List<ParkingAvailabilityResponse> getParkingAvailability(String serviceUuid, String parkingDate) {
        ServiceEntity service = serviceRepository.findByServiceUUid(serviceUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceUuid));

        int capacity = service.getSlot() != null ? service.getSlot() : 0;

        Map<String, Long> bookedByTime = orderRepository
                .countBookingsByTime(service.getId(), parkingDate, OrderStatus.CANCELLED)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return PARKING_TIME_SLOTS.stream()
                .map(time -> {
                    long booked = bookedByTime.getOrDefault(time, 0L);
                    int remaining = Math.max(0, capacity - (int) booked);
                    boolean available = capacity > 0 && booked < capacity;
                    return ParkingAvailabilityResponse.builder()
                            .entryTime(time)
                            .capacity(capacity)
                            .booked(booked)
                            .remaining(remaining)
                            .available(available)
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

    private ServiceOrderResponse mapToResponse(ServiceOrder order) {
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
                .selectedSlot(blankToNull(order.getSelectedSlot()))
                .entryTime(blankToNull(order.getEntryTime()))
                .visitDate(blankToNull(order.getVisitDate()))
                .ticketType(blankToNull(order.getTicketType()))
                .quantity(order.getQuantity())
                .addons(blankToNull(order.getAddons()))
                .notes(blankToNull(order.getNotes()))
                .orderDate(blankToNull(order.getOrderDate()))
                .clientName(order.getClient().getFirstName() + order.getClient().getFatherName())
                .clientEmail(order.getClient().getEmail())
                .build();
    }
}
