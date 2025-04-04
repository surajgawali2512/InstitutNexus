package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Course;
import com.institute.repository.CourseRepository;
import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

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
