package com.example.digitalparking.Controller;

import com.example.digitalparking.Dto.Request.Service.ServiceOrderRequest;
import com.example.digitalparking.Dto.Response.ClientServiceResponse;
import com.example.digitalparking.Dto.Response.ParkingAvailabilityResponse;
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

    @GetMapping("/{serviceUuid}/availability")
    public ResponseEntity<List<ParkingAvailabilityResponse>> getParkingAvailability(
            @PathVariable String serviceUuid,
            @RequestParam String parkingDate,
            @RequestParam(required = false) String duration) {
        return ResponseEntity.ok(clientServiceService.getParkingAvailability(serviceUuid, parkingDate, duration));
    }

    @PostMapping("/orders/{userUuid}")
    public ResponseEntity<ServiceOrderResponse> createOrder(
            @PathVariable String userUuid,
            @Valid @RequestBody ServiceOrderRequest request) {

        ServiceOrder order = clientServiceService.createOrder(request, userUuid);
        ServiceOrderResponse response = clientServiceService.getOrderDetailsResponse(order.getOrderUuid(), userUuid);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    // Cashier endpoint to accept parking appointment on arrival
    @PostMapping("/orders/arrive")
    public ResponseEntity<ServiceOrderResponse> acceptArrival(@RequestParam String plate) {
        return ResponseEntity.ok(clientServiceService.acceptParkingArrival(plate));
    }

    // Cashier endpoint to find a pending parking appointment by plate
    @GetMapping("/orders/appointment")
    public ResponseEntity<ServiceOrderResponse> findAppointment(@RequestParam String plate) {
        return ResponseEntity.ok(clientServiceService.findParkingAppointment(plate));
    }

    // Cashier endpoint to list active (processing) orders by service type
    @RequestMapping(value = "/orders/active", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<List<ServiceOrderResponse>> getActiveOrders(
            @RequestParam(required = false) String serviceType) {
        return ResponseEntity.ok(clientServiceService.getActiveOrders(serviceType));
    }

    @GetMapping("/orders/cashier/reservations")
    public ResponseEntity<List<ServiceOrderResponse>> getCashierReservations(
            @RequestParam(required = false) String parkingDate) {
        return ResponseEntity.ok(clientServiceService.getCashierReservations(parkingDate));
    }

    @GetMapping("/orders/completed")
    public ResponseEntity<List<ServiceOrderResponse>> getCompletedOrders(
            @RequestParam(required = false) String serviceType) {
        return ResponseEntity.ok(clientServiceService.getCompletedOrders(serviceType));
    }

    @PostMapping("/orders/{orderUuid}/checkout")
    public ResponseEntity<ServiceOrderResponse> checkoutParkingOrder(
            @PathVariable String orderUuid,
            @RequestParam(required = false) String paymentMethod) {
        return ResponseEntity.ok(clientServiceService.checkoutParkingOrder(orderUuid, paymentMethod));
    }
}
