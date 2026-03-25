package com.example.mywebsite.entity;

import com.example.mywebsite.model.CartProduct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Double checkoutProductPrice;
    private Double checkoutShippingPrice;
    private Double checkoutPrice;
    private String activeCoupon;

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "cart_id"))
    private List<CartProduct> items = new ArrayList<>();

    public Cart(Long userId) {
        this.userId = userId;
    }

    public Cart() {
    }

    public void addItem(CartProduct product) {
        items.add(product);
    }
    public void removeItem(CartProduct product) {
        items.remove(product);
    }
    public void clearItems() {
        items.clear();
    }
}
