package com.example.digitalparking.ServiceImpl;


import com.example.digitalparking.Entity.Order;
import com.example.digitalparking.Repository.OrderRepository;
import com.example.digitalparking.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order findByOrderUuid(String orderUuid) {
        return orderRepository.findByOrderUuid(orderUuid);
    }
}