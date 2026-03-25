package com.example.mywebsite.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Data
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private LocalDate expires;
    private Long usages;
    private Double discount;

    public Coupon() {
    }

    public Coupon(String code, LocalDate expires, Long usages, Double discount) {
        this.code = code;
        this.expires = expires;
        this.usages = usages;
        this.discount = discount;
    }
}
