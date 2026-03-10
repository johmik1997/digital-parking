package com.example.digitalparking.ServiceImpl.ServiceImpl;

import com.example.digitalparking.Dto.Request.Service.CreateServiceRequest;
import com.example.digitalparking.Dto.Request.Service.UpdateServiceRequest;
import com.example.digitalparking.Dto.Response.ServiceResponse;
import com.example.digitalparking.Entity.Service.ServiceEntity;
import com.example.digitalparking.Repository.Service.ServiceRepository;
import com.example.digitalparking.Service.ServicePro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ServiceImpl implements ServicePro {

    private final ServiceRepository serviceRepository;

    @Override
    public ServiceResponse createService(CreateServiceRequest request) {

        ServiceEntity service = ServiceEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pricingType(request.getPricingType())
                .slot(request.getSlot())
                .currentRate(request.getRate())
                .active(true)
                .build();

        serviceRepository.save(service);
        return mapToResponse(service);
    }

    @Override
    public ServiceResponse updateService(String serviceId, UpdateServiceRequest request) {

        ServiceEntity service = serviceRepository.findByServiceUUid(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (request.getName() != null) {
            service.setName(request.getName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getPricingType() != null) {
            service.setPricingType(request.getPricingType());
        }
        if (request.getSlot() != null) {
            service.setSlot(request.getSlot());
        }
        if (request.getRate() != null) {
            service.setCurrentRate(request.getRate());
        }

        serviceRepository.save(service);
        return mapToResponse(service);
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleService(String serviceId) {
        ServiceEntity service = serviceRepository.findByServiceUUid(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setActive(!service.getActive());
        serviceRepository.save(service);
    }

    private ServiceResponse mapToResponse(ServiceEntity service) {
        return ServiceResponse.builder()
                .serviceUuid(service.getServiceUUid())
                .name(service.getName())
                .description(service.getDescription())
                .pricingType(service.getPricingType())
                .slot(service.getSlot())
                .active(service.getActive())
                .currentRate(service.getCurrentRate())
                .build();
    }
}
