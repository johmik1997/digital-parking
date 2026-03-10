package com.example.digitalparking.Service;

import com.example.digitalparking.Dto.Request.Service.CreateServiceRequest;
import com.example.digitalparking.Dto.Request.Service.UpdateServiceRequest;
import com.example.digitalparking.Dto.Response.ServiceResponse;

import java.util.List;

public interface ServicePro {


    ServiceResponse createService(CreateServiceRequest request);

    ServiceResponse updateService(String serviceId, UpdateServiceRequest request);

    List<ServiceResponse> getAllServices();

    void toggleService(String serviceId);
}
