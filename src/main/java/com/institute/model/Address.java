package com.institute.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String area;
    private String city;
    private String street;
    private String pinCode;
    private String state;

    // No-arg constructor
    public Address() {
    }

    // All-args constructor
    public Address(String area, String city, String street, String pinCode, String state) {
        this.area = area;
        this.city = city;
        this.street = street;
        this.pinCode = pinCode;
        this.state = state;
    }

    // Getters and Setters

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
