package com.example.mywebsite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class CartProduct {
    private Long productId;
    private int quantity;
    private String name;
    private Double price;
    private Double combinedPrice;
    @Lob
    private byte[] image;

    public CartProduct(Long id, int quantity) {
        this.productId = id;
        this.quantity = quantity;
    }

    public CartProduct() {

    }

    public CartProduct(byte[] image, Double price, String name, int quantity, Long productId) {
        this.image = image;
        this.price = price;
        this.name = name;
        this.quantity = quantity;
        this.productId = productId;
        this.combinedPrice = price;
    }
}
