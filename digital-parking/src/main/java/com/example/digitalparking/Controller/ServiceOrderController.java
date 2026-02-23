package com.example.digitalparking.Controller;


import com.example.digitalparking.Dto.Request.Service.ServiceOrderReq;
import com.example.digitalparking.Dto.Response.ServiceOrderResp;
import com.example.digitalparking.Service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Adjust for production
public class ServiceOrderController {

    private final ServiceOrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ServiceOrderResp> createOrder(@RequestBody ServiceOrderReq request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PatchMapping("/{uuid}/pay")
    public ResponseEntity<ServiceOrderResp> payOrder(@PathVariable String uuid) {
        return ResponseEntity.ok(orderService.processPayment(uuid));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ServiceOrderResp>> getPending() {
        // Implementation for getPendingOrders() in service
        return ResponseEntity.ok(orderService.getPendingOrders());
    }
}