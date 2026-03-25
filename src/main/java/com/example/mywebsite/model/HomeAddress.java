package com.example.mywebsite.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HomeAddress {
    private String country;
    private String city;
    private String address;
    private String postalCode;
}
