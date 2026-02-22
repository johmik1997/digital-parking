package com.example.digitalparking.Controller;

import com.example.digitalparking.Dto.Request.Service.ServiceOrderRequest;
import com.example.digitalparking.Dto.Response.ClientServiceResponse;
import com.example.digitalparking.Dto.Response.ServiceOrderResponse;
import com.example.digitalparking.Entity.Service.ServiceEntity;
import com.example.digitalparking.Entity.Service.ServiceOrder;
import com.example.digitalparking.Service.ClientServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client/services")
@RequiredArgsConstructor
public class ClientServiceController {

    private final ClientServiceService clientServiceService;

    @GetMapping("/active")
    public ResponseEntity<List<ClientServiceResponse>> getActiveServices() {
        return ResponseEntity.ok(clientServiceService.getActiveServicesWithCurrentRates());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(clientServiceService.getServiceWithCurrentRate(id));
    }

    @PostMapping("/orders/{userUuid}")
    public ResponseEntity<ServiceOrder> createOrder(
            @PathVariable String userUuid,
            @Valid @RequestBody ServiceOrderRequest request) {

        ServiceOrder order = clientServiceService.createOrder(request, userUuid);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/orders/history/{userUuid}")
    public ResponseEntity<List<ServiceOrderResponse>> getOrderHistory(
            @PathVariable String userUuid) {

        return ResponseEntity.ok(clientServiceService.getClientOrderResponses(userUuid));
    }

    @GetMapping("/orders/{orderUuid}/{userUuid}")
    public ResponseEntity<ServiceOrderResponse> getOrderDetails(
            @PathVariable String orderUuid,
            @PathVariable String userUuid) {

        return ResponseEntity.ok(clientServiceService.getOrderDetailsResponse(orderUuid, userUuid));
    }

    @PutMapping("/orders/{orderUuid}/cancel/{userUuid}")
    public ResponseEntity<ServiceOrder> cancelOrder(
            @PathVariable String orderUuid,
            @PathVariable String userUuid) {

        return ResponseEntity.ok(clientServiceService.cancelOrder(orderUuid, userUuid));
    }

    // Admin endpoint to mark order as completed
    @PutMapping("/admin/orders/{orderUuid}/complete")
    public ResponseEntity<ServiceOrder> completeOrder(
            @PathVariable String orderUuid) {

        return ResponseEntity.ok(clientServiceService.completeOrder(orderUuid));
    }
}
