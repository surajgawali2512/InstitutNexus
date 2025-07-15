//package com.institute.controller;
//
//import com.institute.model.Course;
//import com.institute.model.Department;
//import com.institute.service.DepartmentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/department")
//public class DepartmentContrroller {
//    @Autowired
//    private DepartmentService departmentService;
//    @PostMapping("/add")
//    public Department addDept(@RequestBody Department department){
//        return departmentService.addDepartment(department);
//    }
//
//}
package com.institute.controller;

import com.institute.model.Department;
import com.institute.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = {"X-Institution-Identifier", "Content-Type"},
        exposedHeaders = {"X-Institution-Identifier"}
)
public class DepartmentContrroller {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/add")
    public Department addDept(@RequestBody Department department) {
        return departmentService.addDepartment(department);
    }
    @GetMapping("/get")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
    @PostMapping("/getByName/{name}")
    public List<Department> getDepartmentsByName(@PathVariable String name) {
        return departmentService.getDepartmentsByName(name);
    }
    @GetMapping("/getbyid/{id}")
    public List<Department> getDepartmentsByName(@PathVariable Long id) {
        return departmentService.getDepartmentsByCourseId(id);
    }
    @PostMapping("/delete/{id}")
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
    @PutMapping("/update/{id}")
    public Department updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return departmentService.updateDepartment(id, department);
    }

}
