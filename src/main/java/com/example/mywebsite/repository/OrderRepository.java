package com.example.mywebsite.repository;

import com.example.mywebsite.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    void removeOrderById(Long id);
}
