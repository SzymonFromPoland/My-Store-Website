package com.example.mywebsite.repository;

import com.example.mywebsite.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Product getProductById(Long id);
    Product getFirstByName(String name);
}
