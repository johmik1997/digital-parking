package com.example.digitalparking.Service;


import com.example.digitalparking.Entity.Order;

public interface OrderService {
    Order saveOrder(Order order);
    Order updateOrder(Order order);
    Order findByOrderUuid(String orderUuid);
}