package com.institute.service;

import com.institute.model.Institution;
import com.institute.repository.InstitutionRepository;
import com.institute.config.MultiTenantDataSourceConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;

@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private MultiTenantDataSourceConfig dataSourceConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Institution registerInstitution(Institution institution) {
        // Save institution in the master database
        institution.setDbName("school_" + institution.getName().toLowerCase().replace(" ", "_"));
        institution = institutionRepository.save(institution);

        // Create a new database for the institution
        createNewDatabase(institution.getDbName());

        // Switch to the new database and initialize tables
        DataSource newDataSource = dataSourceConfig.resolveDataSource(institution.getDbName());
        initializeTables(newDataSource);

        return institution;
    }

    private void createNewDatabase(String dbName) {
        String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
        jdbcTemplate.execute(sql);
    }

    private void initializeTables(DataSource dataSource) {
        JdbcTemplate newJdbcTemplate = new JdbcTemplate(dataSource);

        // Create Course Table
        newJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS Course (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "code VARCHAR(255), " +
                "duration BIGINT, "+
                "description VARCHAR(255))");

        // Create Department Table (Embedded in Course)
        newJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS Department (" +
                "deptId BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "departmentName VARCHAR(255), " +
                "hodName VARCHAR(255), " +
                "id BIGINT, " +
                "FOREIGN KEY (id) REFERENCES Course(id) ON DELETE CASCADE)");
        // Create Class Table
        newJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS Classes (" +
                "classId BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "className VARCHAR(255), " +
                "departmentId BIGINT, " +
                "startDate DATE, " +
                "endDate DATE, " +
                "semester VARCHAR(50), " +
                "totalStudents INT, " +
                "FOREIGN KEY (departmentId) REFERENCES Department(deptId) ON DELETE CASCADE)");
    }
}
