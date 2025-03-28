package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Student;
import com.institute.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    public Student addStudent(Student student) {
        String tenant = TenantContext.getCurrentTenant();
        System.out.println("🚀 Current tenant in StudentService: " + tenant);

        if (tenant == null) {
            throw new RuntimeException("❌ No tenant selected! Please provide X-Institution-Identifier.");
        }

        // Switch the database dynamically
        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Insert into dynamically selected database
        String insertQuery = "INSERT INTO student (name, roll_no, email, mobile_no, gender, aadhaar_no) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(insertQuery, student.getName(), student.getRollNo(), student.getEmail(),
                student.getMobileNo(), student.getGender(), student.getAadhaarNo());

        System.out.println("✅ Student added to database: " + tenant);

        return student;
    }

    public List<Student> getAllStudents() {
        String tenant = TenantContext.getCurrentTenant();
        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String selectQuery = "SELECT * FROM student";
        return jdbcTemplate.query(selectQuery, (rs, rowNum) -> {
            System.out.println("✅ Student found: " + rs.getString("name")); // Debugging log
            Student student = new Student();
            student.setId(rs.getLong("id"));  // ✅ Fetching ID from the database
            student.setName(rs.getString("name"));
            student.setRollNo(rs.getString("roll_no"));
            student.setEmail(rs.getString("email"));
            student.setMobileNo(rs.getString("mobile_no"));
            student.setGender(rs.getString("gender"));
            student.setAadhaarNo(rs.getString("aadhaar_no"));
            return student;
        });
    }
}
