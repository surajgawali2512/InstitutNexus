package com.institute.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facultyId;

    private String name;
    private String email;
    private String contactNumber;
private  String JobRole;
    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;
}
