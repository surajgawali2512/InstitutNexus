package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Course;
import com.institute.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DepartmentService {
    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;
//    public Department addDepartment(Department department){
//        String tenant= TenantContext.getCurrentTenant();
//        System.out.println("Current Tenant in CourseService"+tenant);
//        if (tenant==null){
//            throw new RuntimeException("No Tenant Selected");
//        }
//        DataSource dataSource=multiTenantDataSourceConfig.resolveDataSource(tenant);
////        Course savedDepartment=departmentRepository.save(department);
////        return  savedDepartment;
//        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
//        String insertQuery= "INSERT INTO Department(departmentName,hodName,id)"+"Values(?,?,?)";
//        jdbcTemplate.update(insertQuery,department.getDepartmentName(),department.getHodName(),department.getCourse());
//        return  department;
//    }
public Department addDepartment(Department department) {
    String tenant = TenantContext.getCurrentTenant();
    System.out.println("Current Tenant in CourseService: " + tenant);

    if (tenant == null) {
        throw new RuntimeException("No Tenant Selected");
    }

    // Resolve the data source based on the current tenant
    DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    // Check if the course ID exists in the Course table
    String checkCourseQuery = "SELECT COUNT(*) FROM Course WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(checkCourseQuery, Integer.class, department.getCourse().getId());

    if (count == null || count == 0) {
        throw new RuntimeException("Course not found with ID: " + department.getCourse().getId());
    }

    // Insert the department data
    String insertQuery = "INSERT INTO Department(departmentName, hodName,id) VALUES (?, ?, ?)";
    jdbcTemplate.update(insertQuery, department.getDepartmentName(), department.getHodName(), department.getCourse().getId());
    // Retrieve the last inserted ID
    String idQuery = "SELECT LAST_INSERT_ID()";
    Long generatedId = jdbcTemplate.queryForObject(idQuery, Long.class);
    department.setDeptId(generatedId);

    // Fetch the course data to include in the response
    String courseQuery = "SELECT id, name, code, duration, description FROM Course WHERE id = ?";
    Course course = jdbcTemplate.queryForObject(courseQuery, new Object[]{department.getCourse().getId()}, (rs, rowNum) -> {
        Course c = new Course();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setCode(rs.getString("code"));
        c.setDuration(rs.getLong("duration"));
        c.setDescription(rs.getString("description"));
        return c;
    });

    department.setCourse(course);  // Set the course in the department object

    return department;
}

}
