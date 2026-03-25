package com.example.mywebsite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private int stock;
    private Boolean configurable;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> variants;
    @Lob
    private byte[] image;
}
