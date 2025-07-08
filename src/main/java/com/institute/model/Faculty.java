package com.institute.model;

import jakarta.persistence.*;

@Entity
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facultyId;

    private String name;
    private String email;
    private String contactNumber;
    private String jobRole;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;

    // Constructors
    public Faculty() {
    }

    public Faculty(Long facultyId, String name, String email, String contactNumber, String jobRole, Address address, Department department) {
        this.facultyId = facultyId;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.jobRole = jobRole;
        this.address = address;
        this.department = department;
    }

    // Getters and Setters

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
