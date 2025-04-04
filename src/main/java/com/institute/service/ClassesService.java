package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Classes;
import com.institute.model.Course;
import com.institute.model.Department;
import com.institute.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.sql.DataSource;

@Service
public class ClassesService {
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private  MultiTenantDataSourceConfig multiTenantDataSourceConfig;
    public Classes addClass(@RequestBody Classes newClass){
        String tenant= TenantContext.getCurrentTenant();
        System.out.println("Current Tenant in CourseService"+tenant);
        if (tenant==null){
            throw new RuntimeException("No Tenant Selected");
        }
        // Resolve the data source based on the current tenant
        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Extract the department ID from the Department object
        Long departmentId = newClass.getDepartment().getDeptId();

// Check if the department ID exists in the Department table
        String checkDepartmentQuery = "SELECT COUNT(*) FROM Department WHERE deptId = ?";
        Integer count = jdbcTemplate.queryForObject(checkDepartmentQuery, Integer.class, departmentId);

//        // Check if the department ID exists in the Department table
//        String checkDepartmentQuery = "SELECT COUNT(*) FROM Department WHERE deptId = ?";
//        Integer count = jdbcTemplate.queryForObject(checkDepartmentQuery, Integer.class, newClass.getDepartment());

        if (count == null || count == 0) {
            throw new RuntimeException("Department not found with ID: " + newClass.getDepartment());
        }

        // Insert the class data
        String insertQuery = "INSERT INTO Classes(className, departmentId, startDate, endDate, semester, totalStudents) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertQuery, newClass.getClassName(),  newClass.getDepartment().getDeptId(), newClass.getStartDate(), newClass.getEndDate(), newClass.getSemester(), newClass.getTotalStudents());

        // Retrieve the last inserted ID
        String idQuery = "SELECT LAST_INSERT_ID()";
        Long generatedId = jdbcTemplate.queryForObject(idQuery, Long.class);
        newClass.setClassId(generatedId);

        // Fetch the department data to include in the response
        String departmentQuery = "SELECT deptId, departmentName, hodName, id FROM Department WHERE deptId = ?";
        Department department = jdbcTemplate.queryForObject(departmentQuery, new Object[]{newClass.getDepartment().getDeptId()}, (rs, rowNum) -> {
            Department d = new Department();
            d.setDeptId(rs.getLong("deptId"));
            d.setDepartmentName(rs.getString("departmentName"));
            d.setHodName(rs.getString("hodName"));
            Course course = new Course();
            course.setId(rs.getLong("id"));
            d.setCourse(course);
            return d;
        });

        newClass.setDepartment(department);  // Set the department in the class object

        return newClass;
    }
}
