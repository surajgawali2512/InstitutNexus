package com.institute.controller;

import com.institute.model.Course;
import com.institute.model.Department;
import com.institute.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/department")
public class DepartmentContrroller {
    @Autowired
    private DepartmentService departmentService;
    @PostMapping("/add")
    public Department addDept(@RequestBody Department department){
        return departmentService.addDepartment(department);
    }
}
