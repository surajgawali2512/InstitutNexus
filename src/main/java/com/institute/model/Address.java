package com.institute.model;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String area;
    private String city;
    private String street;
    private String pinCode;
    private String state;
}

