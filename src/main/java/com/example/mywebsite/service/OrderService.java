package com.example.mywebsite.service;

import com.example.mywebsite.entity.Order;
import com.example.mywebsite.entity.Product;
import com.example.mywebsite.entity.User;
import com.example.mywebsite.model.HomeAddress;
import com.example.mywebsite.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    public void createOrder(Principal principal, Double price, LocalDate date, HomeAddress address, Double discount) {
        User user = userService.getUser(principal);
        Order order = new Order();
        order.setPrice(price);
        order.setDate(date);
        order.setAddress(address);
        order.setUserId(user.getId());
        order.setDiscount(discount);
        order.setStatus("PENDING");

        order.setUserName(user.getFirstName());
        order.setUserLastName(user.getLastName());

        order.setActualPrice(price * (1 - discount / 100));

        orderRepository.save(order);
    }

    public void changeStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        orderRepository.save(order);
    }

    public List<Order> getOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public void removeOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public void clearOrders() {
        orderRepository.deleteAll();
    }
}
