package com.institute.controller;

import com.institute.model.Course;
import com.institute.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @PostMapping("/add")
    public Course addCourses(@RequestBody Course course){
        return courseService.addCourse(course);
    }
}
