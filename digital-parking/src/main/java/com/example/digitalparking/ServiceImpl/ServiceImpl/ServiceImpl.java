package com.example.digitalparking.ServiceImpl.ServiceImpl;

import com.example.digitalparking.Dto.Request.Service.CreateServiceRequest;
import com.example.digitalparking.Dto.Request.Service.UpdateRateRequest;
import com.example.digitalparking.Dto.Response.ServiceResponse;
import com.example.digitalparking.Entity.Service.ServiceEntity;
import com.example.digitalparking.Entity.Service.ServiceRate;
import com.example.digitalparking.Repository.Service.ServiceRateRepository;
import com.example.digitalparking.Repository.Service.ServiceRepository;
import com.example.digitalparking.Service.ServicePro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ServiceImpl implements ServicePro {

    private final ServiceRepository serviceRepository;
    private final ServiceRateRepository rateRepository;

    @Override
    public ServiceResponse createService(CreateServiceRequest request) {

        ServiceEntity service = ServiceEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pricingType(request.getPricingType())
                .active(true)
                .build();

        serviceRepository.save(service);

        System.out.println("seeeeeee" + service.getServiceUUid());

        return mapToResponse(service, null);
    }

    @Override
    public ServiceResponse updateRate(String serviceId, UpdateRateRequest request) {

        ServiceEntity service = serviceRepository.findByServiceUUid(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        ServiceRate rate = ServiceRate.builder()
                .service(service)
                .rate(request.getRate())
                .effectiveFrom(LocalDate.now())
                .build();

        rateRepository.save(rate);

        return mapToResponse(service, rate.getRate());
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(service -> {
                    var latestRate = rateRepository
                            .findTopByServiceIdOrderByEffectiveFromDesc(service.getId())
                            .map(ServiceRate::getRate)
                            .orElse(null);

                    return mapToResponse(service, latestRate);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void toggleService(String serviceId) {
        ServiceEntity service = serviceRepository.findByServiceUUid(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setActive(!service.getActive());
        serviceRepository.save(service);
    }

    private ServiceResponse mapToResponse(ServiceEntity service, java.math.BigDecimal rate) {
        return ServiceResponse.builder()
                .serviceUuid(service.getServiceUUid())
                .name(service.getName())
                .description(service.getDescription())
                .pricingType(service.getPricingType())
                .active(service.getActive())
                .currentRate(rate)
                .build();
    }
}
