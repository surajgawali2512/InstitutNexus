package com.institute.controller;

import com.institute.model.Course;
import com.institute.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/add")
    public Course addCourses(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @PostMapping("/getById/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping("/delete/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    @PutMapping("/update/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    @GetMapping("/get")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }
}
