package com.example.mywebsite.service;

import com.example.mywebsite.entity.Cart;
import com.example.mywebsite.entity.Coupon;
import com.example.mywebsite.repository.CartRepository;
import com.example.mywebsite.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;


    public CouponService(CouponRepository couponRepository, CartService cartService, CartRepository cartRepository) {
        this.couponRepository = couponRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }

    public Coupon getByCode(String code) {
        return couponRepository.findByCode(code).orElseThrow();
    }

    public Coupon getById(Long id) {
        return couponRepository.findById(id).orElseThrow();
    }

    public void createCoupon(String code, LocalDate expires, Long usages, Double discount) {
        Coupon coupon = new Coupon(code, expires, usages, discount);
        couponRepository.save(coupon);
    }

    public void removeCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }

    public boolean validate(Coupon coupon) {
        LocalDate expires = coupon.getExpires();
        LocalDate now = LocalDate.now();
        if (Optional.ofNullable(coupon.getUsages()).orElse(0L).equals(0L)) {
            return false;
        }

        return !now.isAfter(expires);
    }

    public String assignCoupon(Principal principal, Coupon coupon) {
        Cart cart = cartService.getCart(principal);
        boolean valid = validate(coupon);
        cart.setActiveCoupon(valid ? coupon.getCode() : null);
        cartRepository.save(cart);
        return valid ? "successful=added" : "error=expired";
    }

    public boolean useCoupon(Coupon coupon) {
        if (!validate(coupon)) return false;
        coupon.setUsages(coupon.getUsages() - 1);

        couponRepository.save(coupon);

        return true;
    }
}
