package com.example.mywebsite.entity;


import com.example.mywebsite.model.HomeAddress;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String userName;
    private String userLastName;
    private Double price;
    private Double discount;
    private Double actualPrice;
    private LocalDate date;
    private HomeAddress address;
    private String status;
}
