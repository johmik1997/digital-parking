package com.example.digitalparking.ServiceImpl.ServiceImpl;

import com.example.digitalparking.Dto.Request.Service.ServiceOrderRequest;
import com.example.digitalparking.Dto.Response.ClientServiceResponse;
import com.example.digitalparking.Dto.Response.ServiceOrderResponse;
import com.example.digitalparking.Entity.Service.*;
import com.example.digitalparking.Entity.User.UserEntity;
import com.example.digitalparking.Enum.OrderStatus;
import com.example.digitalparking.Repository.Service.ServiceOrderRepository;
import com.example.digitalparking.Repository.Service.ServiceRateRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceServiceImpl implements ClientServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceRateRepository rateRepository;
    private final ServiceOrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public List<ClientServiceResponse> getActiveServicesWithCurrentRates() {

        List<ServiceEntity> activeServices = serviceRepository.findByActiveTrue();

        return activeServices.stream().map(service -> {

            BigDecimal rate = rateRepository
                    .findCurrentRateByServiceId(service.getId())
                    .map(r -> r.getRate())
                    .orElse(null);

            return ClientServiceResponse.builder()
                    .serviceUuid(service.getServiceUUid())
                    .name(service.getName())
                    .description(service.getDescription())
                    .pricingType(service.getPricingType())
                    .active(service.getActive())
                    .currentRate(rate)
                    .build();

        }).toList();
    }


    @Override
    public ServiceEntity getServiceWithCurrentRate(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));

        rateRepository.findCurrentRateByServiceId(serviceId)
                .ifPresent(service::setCurrentRate);

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

        // Get current rate
        ServiceRate currentRate = rateRepository.findCurrentRateByServiceId(service.getId())
                .orElseThrow(() -> new IllegalStateException("No active rate found for this service"));

        // Validate pricing
        validateOrderPricing(request, service, currentRate);

        // Build order
        ServiceOrder order = ServiceOrder.builder()
                .service(service)
                .client(client)
                .rateApplied(currentRate)
                .durationHours(Double.valueOf(request.getDuration()))
                .totalAmount(BigDecimal.valueOf(request.getTotalAmount()))
                .status(OrderStatus.PENDING)
                .build();

        ServiceOrder savedOrder = orderRepository.save(order);
        log.info("Service order created successfully with id: {}", savedOrder.getId());

        return savedOrder;
    }

    private void validateOrderPricing(ServiceOrderRequest request, ServiceEntity service, ServiceRate currentRate) {
        BigDecimal calculatedTotal;

        if ("HOURLY".equals(service.getPricingType())) {
//            if (request.getDuration() == null || request.getDuration() <= 0) {
//                throw new IllegalArgumentException("Duration is required for hourly services");
//            }
            calculatedTotal = currentRate.getRate().multiply(BigDecimal.valueOf(Long.parseLong(request.getDuration())));
        } else {
            calculatedTotal = currentRate.getRate();
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
                .OrderUuid(order.getOrderUuid())
                .serviceName(order.getService().getName())
                .serviceDescription(order.getService().getDescription())
                .pricingType(order.getService().getPricingType())
                .durationHours(order.getDurationHours())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .rateUuid(order.getRateApplied().getRateUuid())
                .rateApplied(order.getRateApplied().getRate())
                .clientName(order.getClient().getFirstName() + order.getClient().getFatherName())
                .clientEmail(order.getClient().getEmail())
                .build();
    }
}
