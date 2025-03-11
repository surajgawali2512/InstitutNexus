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

        // Create Student Table
        newJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS Student (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "roll_no VARCHAR(50), " +
                "email VARCHAR(255), " +
                "mobile_no VARCHAR(20), " +
                "gender VARCHAR(10), " +
                "aadhaar_no VARCHAR(20))");

        // Create Address Table (Embedded in Student)
        newJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS Teacher (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "subject VARCHAR(255), " +
                "email VARCHAR(255), " +
                "phone VARCHAR(20))");
    }
}
