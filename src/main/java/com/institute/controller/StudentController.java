package com.institute.controller;

import com.institute.model.Student;
import com.institute.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/")
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }
}

