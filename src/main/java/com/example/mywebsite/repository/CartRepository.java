package com.example.mywebsite.repository;

import com.example.mywebsite.entity.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart, Long> {
    Cart findCartByUserId(Long id);
}
