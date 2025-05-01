package com.institute.service;

import com.institute.config.TenantContext;
import com.institute.model.Institution;
import com.institute.repository.InstitutionRepository;
import com.institute.config.MultiTenantDataSourceConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Optional;

@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private MultiTenantDataSourceConfig dataSourceConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Institution updateEmail(Long id, String email) {
//        String tenant = TenantContext.getCurrentTenant();
//        System.out.println("Current Tenant in CourseService: " + tenant);
//
//        if (tenant == null) {
//            throw new RuntimeException("No Tenant Selected");
//        }
//
//        // Resolve the data source based on the current tenant
//        DataSource dataSource = dataSourceConfig.resolveDataSource(tenant);
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Optional<Institution> optionalInstitution = institutionRepository.findById(id);

        if (optionalInstitution.isEmpty()) {
            throw new RuntimeException("Institution not found with ID: " + id);
        }
        Institution institution = optionalInstitution.get();
        institution.setEmail(email);
//        institution.setPassword(newPassword); // Consider encrypting this
        return institutionRepository.save(institution);
    }

    @Transactional
    public Institution updateInstitute(String email, Institution updatedInstitution) {
        Optional<Institution> optionalInstitution = institutionRepository.findByEmail(email);

        if (optionalInstitution.isEmpty()) {
            throw new RuntimeException("Institution not found with email: " + email);
        }

        Institution institution = optionalInstitution.get();

        // Update fields only if they're not null
        if (updatedInstitution.getName() != null) {
            institution.setName(updatedInstitution.getName());
        }
        if (updatedInstitution.getAddress() != null) {
            institution.setAddress(updatedInstitution.getAddress());
        }
        if (updatedInstitution.getPhone() != null) {
            institution.setPhone(updatedInstitution.getPhone());
        }
        if (updatedInstitution.getUsername() != null) {
            institution.setUsername(updatedInstitution.getUsername());
        }
        if (updatedInstitution.getPassword() != null) {
            institution.setPassword(updatedInstitution.getPassword()); // Encrypt this if needed
        }
        if (updatedInstitution.getStartDate() != null) {
            institution.setStartDate(updatedInstitution.getStartDate());
        }
        if (updatedInstitution.getEndDate() != null) {
            institution.setEndDate(updatedInstitution.getEndDate());
        }
        if (updatedInstitution.getStatus() != null) {
            institution.setStatus(updatedInstitution.getStatus());
        }
        if (updatedInstitution.getDbName() != null) {
            institution.setDbName(updatedInstitution.getDbName());
        }

        return institutionRepository.save(institution);
    }


    @Transactional
    public Institution updatePassword(Long id, String password) {
        Optional<Institution> optionalInstitution = institutionRepository.findById(id);
        if (optionalInstitution.isEmpty()) {
            throw new RuntimeException("Institution not found with ID: " + id);
        }
        Institution institution = optionalInstitution.get();
        institution.setPassword(passwordEncoder.encode(password));
        return institutionRepository.save(institution);
    }

    @Transactional
    public Institution login(String email, String rawPassword) {
        Optional<Institution> optionalInstitution = institutionRepository.findByEmail(email);

        if (optionalInstitution.isEmpty()) {
            throw new RuntimeException("Institution not found with email: " + email);
        }

        Institution institution = optionalInstitution.get();

        if (!passwordEncoder.matches(rawPassword, institution.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Set tenant in context (background)
        TenantContext.setCurrentTenant(institution.getUsername());

        // Return a sanitized copy of the institution
        Institution safeInstitution = new Institution();
        safeInstitution.setId(institution.getId());
        safeInstitution.setName(institution.getName());
        safeInstitution.setAddress(institution.getAddress());
        safeInstitution.setEmail(institution.getEmail());
        safeInstitution.setPhone(institution.getPhone());
        safeInstitution.setUsername(institution.getUsername());
        safeInstitution.setStartDate(institution.getStartDate());
        safeInstitution.setEndDate(institution.getEndDate());
        safeInstitution.setStatus(institution.getStatus());
        // Do NOT set password or dbName

        return safeInstitution;
    }



//    @Transactional
//    public Institution login(String email, String rawPassword) {
//        Optional<Institution> optionalInstitution = institutionRepository.findByEmail(email);
//
//        if (optionalInstitution.isEmpty()) {
//            throw new RuntimeException("Institution not found with email: " + email);
//        }
//
//        Institution institution = optionalInstitution.get();
//
//        if (!passwordEncoder.matches(rawPassword, institution.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//
//        // Set tenant in context (background)
//        TenantContext.setCurrentTenant(institution.getUsername());
//
//        return institution;
//    }


    @Transactional
    public Institution registerInstitution(Institution institution) {
        // Check if institution already exists
        Optional<Institution> existingInstitution = institutionRepository.findByNameIgnoreCase(institution.getName());

        if (existingInstitution.isPresent()) {
            throw new RuntimeException("Institution already registered with name: " + institution.getName());
        }


        // Hash password using BCrypt
        String encodedPassword = passwordEncoder.encode(institution.getPassword());
        institution.setPassword(encodedPassword);

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
                "duration BIGINT, " +
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
