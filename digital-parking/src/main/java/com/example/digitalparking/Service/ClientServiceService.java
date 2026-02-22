package com.example.digitalparking.Service;

import com.example.digitalparking.Dto.Request.Service.ServiceOrderRequest;
import com.example.digitalparking.Dto.Response.ClientServiceResponse;
import com.example.digitalparking.Dto.Response.ServiceOrderResponse;
import com.example.digitalparking.Entity.Service.ServiceEntity;
import com.example.digitalparking.Entity.Service.ServiceOrder;


import java.util.List;

public interface ClientServiceService {

    // Service methods
    List<ClientServiceResponse> getActiveServicesWithCurrentRates();

    ServiceEntity getServiceWithCurrentRate(Long serviceId);

    // Order methods
    ServiceOrder createOrder(ServiceOrderRequest request, String clientId);

    List<ServiceOrder> getClientOrders(String clientId);

    List<ServiceOrderResponse> getClientOrderResponses(String clientId);

    ServiceOrder getOrderDetails(String orderId, String clientId);

    ServiceOrderResponse getOrderDetailsResponse(String orderId, String clientId);

    ServiceOrder cancelOrder(String orderId, String clientId);

    ServiceOrder completeOrder(String orderId);
}