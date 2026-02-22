package com.example.digitalparking.Service;

import com.example.digitalparking.Dto.Request.Service.CreateServiceRequest;
import com.example.digitalparking.Dto.Request.Service.UpdateRateRequest;
import com.example.digitalparking.Dto.Response.ServiceResponse;

import java.util.List;

public interface ServicePro {


    ServiceResponse createService(CreateServiceRequest request);

    ServiceResponse updateRate(String serviceId, UpdateRateRequest request);

    List<ServiceResponse> getAllServices();

    void toggleService(String serviceId);
}
