package com.example.digitalparking.Service;


import com.example.digitalparking.Dto.Request.Service.ServiceOrderReq;
import com.example.digitalparking.Dto.Response.ServiceOrderResp;


import java.util.List;

public interface ServiceOrderService {
    ServiceOrderResp createOrder(ServiceOrderReq request);
    ServiceOrderResp processPayment(String orderUuid);
    List<ServiceOrderResp> getPendingOrders();
}