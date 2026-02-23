package com.example.digitalparking.ServiceImpl.ServiceImpl;

import com.example.digitalparking.Dto.Request.Service.ServiceOrderReq;
import com.example.digitalparking.Dto.Response.ServiceOrderResp;
import com.example.digitalparking.Entity.Service.ServiceEntity;
import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Entity.Service.ServiceRate;
import com.example.digitalparking.Enum.OrderStatus;
import com.example.digitalparking.Repository.Service.ServiceOrRepository;
import com.example.digitalparking.Repository.Service.ServiceRepository;
import com.example.digitalparking.Repository.UserRepository;
import com.example.digitalparking.Service.ServiceOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository; // Assuming UserEntity repo exists

    @Override
    @Transactional
    public ServiceOrderResp createOrder(ServiceOrderReq request) {
        ServiceEntity service = serviceRepository.findByServiceUUid(request.serviceUuid())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Get the most recent rate
        ServiceRate currentRate = service.getRates().stream()
                .sorted(Comparator.comparing(ServiceRate::getEffectiveFrom).reversed())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active rate found"));

        BigDecimal total;
        if ("HOURLY".equals(service.getPricingType())) {
            total = currentRate.getRate().multiply(BigDecimal.valueOf(request.durationHours()));
        } else {
            total = currentRate.getRate();
        }

        ServiceOrder order = ServiceOrder.builder()
                .service(service)
                .rateApplied(currentRate)
                .totalAmount(total)
                .durationHours(request.durationHours())
                .status(OrderStatus.PENDING)
                .build();

        ServiceOrder saved = orderRepository.save(order);
        return mapToResponse(saved);
    }


    @Override
    @Transactional
    public ServiceOrderResp processPayment(String orderUuid) {
        ServiceOrder order = orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.COMPLETED); // Or PAID
        order.setCompletedAt(LocalDateTime.now());

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public List<ServiceOrderResp> getPendingOrders() {
        return List.of();
    }

    private ServiceOrderResp mapToResponse(ServiceOrder order) {
        return new ServiceOrderResp(
                order.getOrderUuid(),
                order.getService().getName(),
                order.getClient() != null ? order.getClient().getFirstName() : "Guest",
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getCompletedAt()
        );
    }
}