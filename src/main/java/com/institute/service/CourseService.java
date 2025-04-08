package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Course;
import com.institute.repository.CourseRepository;
import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;


    public List<Course> getAllCourses() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            throw new RuntimeException("No Tenant Selected");
        }

        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String selectQuery = "SELECT * FROM Course";

        List<Course> courses = jdbcTemplate.query(
                selectQuery,
                new BeanPropertyRowMapper<>(Course.class)
        );

        return courses;
    }

    public Course getCourseById(Long id) {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            throw new RuntimeException("No Tenant Selected");
        }

        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String selectQuery = "SELECT * FROM COURSE WHERE id = ?";
        return jdbcTemplate.queryForObject(
                selectQuery,
                new BeanPropertyRowMapper<>(Course.class),
                id
        );
    }
    public void deleteCourse(Long id) {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            throw new RuntimeException("No Tenant Selected");
        }

        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String deleteQuery = "DELETE FROM COURSE WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(deleteQuery, id);

        if (rowsDeleted == 0) {
            throw new RuntimeException("Course with id " + id + " not found.");
        }
    }
    public Course updateCourse(Long id, Course updatedCourse) {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            throw new RuntimeException("No Tenant Selected");
        }

        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String updateQuery = "UPDATE COURSE SET name = ?, code = ?, duration = ?, description = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateQuery,
                updatedCourse.getName(),
                updatedCourse.getCode(),
                updatedCourse.getDuration(),
                updatedCourse.getDescription(),
                id);

        if (rowsAffected == 0) {
            throw new RuntimeException("Course with id " + id + " not found.");
        }

        updatedCourse.setId(id);
        return updatedCourse;
    }

    public Course addCourse(Course course){
        String tenant= TenantContext.getCurrentTenant();
        System.out.println("Current Tenant in CourseService: "+tenant);
        if (tenant==null){
           throw new RuntimeException("No Tenant Selected");
        }
        DataSource dataSource=multiTenantDataSourceConfig.resolveDataSource(tenant);
//        Course savedCourse=courseRepository.save(course);
//        return  savedCourse;
        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource);
        String insertQuery= "INSERT INTO COURSE(name,code,duration,description)"+"Values(?,?,?,?)";
        jdbcTemplate.update(insertQuery,course.getName(),course.getCode(),course.getDuration(),course.getDescription());
        // Retrieve the last inserted ID
        String idQuery = "SELECT LAST_INSERT_ID()";
        Long generatedId = jdbcTemplate.queryForObject(idQuery, Long.class);
        course.setId(generatedId);

        return  course;
    }
}
