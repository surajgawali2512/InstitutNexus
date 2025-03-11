package com.institute.service;


import com.institute.config.TenantContext;
import com.institute.model.Student;
import com.institute.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

//    public Student addStudent(Student student) {
//        return studentRepository.save(student);
//    }
    public Student addStudent(Student student) {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            throw new RuntimeException("No tenant selected! Please provide X-Tenant-ID.");
        }
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}

