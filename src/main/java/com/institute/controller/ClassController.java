package com.institute.controller;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.model.Classes;
import com.institute.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    @Autowired
    private ClassesService classesService;

    @PostMapping("/add")
    public Classes addClass(@RequestBody Classes classes){
        return classesService.addClass(classes);
    }
}
