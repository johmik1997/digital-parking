package com.example.digitalparking.Controller;


import com.example.digitalparking.Dto.Request.Service.CreateServiceRequest;
import com.example.digitalparking.Dto.Request.Service.UpdateRateRequest;
import com.example.digitalparking.Dto.Response.ServiceResponse;
import com.example.digitalparking.Service.ServicePro;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServicePro serviceService;

    @PostMapping
    public ServiceResponse createService(@RequestBody CreateServiceRequest request) {
        return serviceService.createService(request);
    }

    @PostMapping("/{ServiceUuid}/rate")
    public ServiceResponse updateRate(
            @PathVariable String ServiceUuid,
            @RequestBody UpdateRateRequest request) {
        return serviceService.updateRate(ServiceUuid, request);
    }

    @GetMapping
    public List<ServiceResponse> getAllServices() {
        return serviceService.getAllServices();
    }

    @PutMapping("/{ServiceUuid}/toggle")
    public void toggleService(@PathVariable String ServiceUuid) {
        serviceService.toggleService(ServiceUuid);
    }
}
