package com.example.mywebsite.repository;

import com.example.mywebsite.entity.Coupon;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CouponRepository extends CrudRepository<Coupon, Long> {
    void removeCouponById(Long couponId);
    Optional<Coupon> findByCode(String code);
}
