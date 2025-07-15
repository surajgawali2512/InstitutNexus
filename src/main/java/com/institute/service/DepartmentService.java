package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Course;
import com.institute.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    private JdbcTemplate getJdbcTemplateForCurrentTenant() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) throw new RuntimeException("No Tenant Selected");

        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        return new JdbcTemplate(dataSource);
    }

    public Department addDepartment(Department department) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

        String checkCourseQuery = "SELECT COUNT(*) FROM Course WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkCourseQuery, Integer.class, department.getCourse().getId());

        if (count == null || count == 0) {
            throw new RuntimeException("Course not found with ID: " + department.getCourse().getId());
        }

        String insertQuery = "INSERT INTO Department(departmentName, hodName, id) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, department.getDepartmentName(), department.getHodName(), department.getCourse().getId());

        String idQuery = "SELECT LAST_INSERT_ID()";
        Long generatedId = jdbcTemplate.queryForObject(idQuery, Long.class);
        department.setDeptId(generatedId);

        // Fetch course
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

        department.setCourse(course);
        return department;
    }

//    public List<Department> getAllDepartments() {
//        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();
//        String selectQuery = "SELECT * FROM Department";
//        return jdbcTemplate.query(selectQuery, new BeanPropertyRowMapper<>(Department.class));
//    }
public List<Department> getAllDepartments() {
    JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

    String query = """
        SELECT d.deptId, d.departmentName, d.hodName,
               c.id AS courseId, c.Name AS courseName
        FROM Department d
        LEFT JOIN Course c ON d.id = c.id
    """;

    return jdbcTemplate.query(query, (rs, rowNum) -> {
        Department department = new Department();
        department.setDeptId(rs.getLong("deptId"));
        department.setDepartmentName(rs.getString("departmentName"));
        department.setHodName(rs.getString("hodName"));

        Course course = new Course();
        course.setId(rs.getLong("courseId"));
        course.setName(rs.getString("courseName"));

        department.setCourse(course);
        return department;
    });
}


    public List<Department> getDepartmentsByName(String name) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();
        String selectQuery = "SELECT * FROM Department WHERE departmentName LIKE ?";
        return jdbcTemplate.query(selectQuery, new BeanPropertyRowMapper<>(Department.class), "%" + name + "%");
    }
    public List<Department> getDepartmentsByCourseId(Long id) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();
        String selectQuery = "SELECT * FROM Department WHERE id = ?";
        return jdbcTemplate.query(selectQuery, new BeanPropertyRowMapper<>(Department.class), id);
    }

//    public Department updateDepartment(Long id, Department updatedDepartment) {
//        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();
//        String updateQuery = "UPDATE Department SET departmentName = ?, hodName = ? WHERE deptId = ?";
//        int rowsAffected = jdbcTemplate.update(updateQuery,
//                updatedDepartment.getDepartmentName(),
//                updatedDepartment.getHodName(),
//                id);
//
//        if (rowsAffected == 0) {
//            throw new RuntimeException("Department with id " + id + " not found.");
//        }
//
//        updatedDepartment.setDeptId(id);
//        return updatedDepartment;
//    }
public Department updateDepartment(Long id, Department updatedDepartment) {
    JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

    String updateQuery = "UPDATE Department SET departmentName = ?, hodName = ?, id = ? WHERE deptId = ?";
    int rowsAffected = jdbcTemplate.update(updateQuery,
            updatedDepartment.getDepartmentName(),
            updatedDepartment.getHodName(),
            updatedDepartment.getCourse().getId(),
            id);

    if (rowsAffected == 0) {
        throw new RuntimeException("Department with id " + id + " not found.");
    }

    updatedDepartment.setDeptId(id);

    // Fetch course (only id and name)
    String courseQuery = "SELECT id, name FROM Course WHERE id = ?";
    Course course = jdbcTemplate.queryForObject(courseQuery, new Object[]{updatedDepartment.getCourse().getId()},
            (rs, rowNum) -> {
                Course c = new Course();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                return c;
            });

    updatedDepartment.setCourse(course);
    return updatedDepartment;
}


    public void deleteDepartment(Long id) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();
        String deleteQuery = "DELETE FROM Department WHERE deptId = ?";
        int rowsDeleted = jdbcTemplate.update(deleteQuery, id);

        if (rowsDeleted == 0) {
            throw new RuntimeException("Department with id " + id + " not found.");
        }
    }
}

//package com.institute.service;
//
//import com.institute.config.MultiTenantDataSourceConfig;
//import com.institute.config.TenantContext;
//import com.institute.model.Course;
//import com.institute.model.Department;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.sql.DataSource;
//
//@Service
//public class DepartmentService {
//    @Autowired
//    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;
//
//public Department addDepartment(Department department) {
//    String tenant = TenantContext.getCurrentTenant();
//    System.out.println("Current Tenant in CourseService: " + tenant);
//
//    if (tenant == null) {
//        throw new RuntimeException("No Tenant Selected");
//    }
//
//    // Resolve the data source based on the current tenant
//    DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//
//    // Check if the course ID exists in the Course table
//    String checkCourseQuery = "SELECT COUNT(*) FROM Course WHERE id = ?";
//    Integer count = jdbcTemplate.queryForObject(checkCourseQuery, Integer.class, department.getCourse().getId());
//
//    if (count == null || count == 0) {
//        throw new RuntimeException("Course not found with ID: " + department.getCourse().getId());
//    }
//
//    // Insert the department data
//    String insertQuery = "INSERT INTO Department(departmentName, hodName,id) VALUES (?, ?, ?)";
//    jdbcTemplate.update(insertQuery, department.getDepartmentName(), department.getHodName(), department.getCourse().getId());
//    // Retrieve the last inserted ID
//    String idQuery = "SELECT LAST_INSERT_ID()";
//    Long generatedId = jdbcTemplate.queryForObject(idQuery, Long.class);
//    department.setDeptId(generatedId);
//
//    // Fetch the course data to include in the response
//    String courseQuery = "SELECT id, name, code, duration, description FROM Course WHERE id = ?";
//    Course course = jdbcTemplate.queryForObject(courseQuery, new Object[]{department.getCourse().getId()}, (rs, rowNum) -> {
//        Course c = new Course();
//        c.setId(rs.getLong("id"));
//        c.setName(rs.getString("name"));
//        c.setCode(rs.getString("code"));
//        c.setDuration(rs.getLong("duration"));
//        c.setDescription(rs.getString("description"));
//        return c;
//    });
//
//    department.setCourse(course);  // Set the course in the department object
//
//    return department;
//}
//
//}
