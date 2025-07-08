package com.institute.service;

import com.institute.config.MultiTenantDataSourceConfig;
import com.institute.config.TenantContext;
import com.institute.model.Address;
import com.institute.model.Department;
import com.institute.model.Faculty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private MultiTenantDataSourceConfig multiTenantDataSourceConfig;

    private JdbcTemplate getJdbcTemplateForCurrentTenant() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) throw new RuntimeException("No Tenant Selected");
        DataSource dataSource = multiTenantDataSourceConfig.resolveDataSource(tenant);
        return new JdbcTemplate(dataSource);
    }

    // Create Faculty
    public Faculty addFaculty(Faculty faculty) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

        // Check if department exists
        String checkDepartmentQuery = "SELECT COUNT(*) FROM Department WHERE deptId = ?";
        Integer count = jdbcTemplate.queryForObject(checkDepartmentQuery, Integer.class, faculty.getDepartment().getDeptId());
        if (count == null || count == 0) {
            throw new RuntimeException("Department not found with ID: " + faculty.getDepartment().getDeptId());
        }

        // Insert Faculty
        String insertQuery = """
            INSERT INTO Faculty (name, email, contactNumber, jobRole, area, city, street, pinCode, state, departmentId)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(insertQuery,
                faculty.getName(),
                faculty.getEmail(),
                faculty.getContactNumber(),
                faculty.getJobRole(),
                faculty.getAddress().getArea(),
                faculty.getAddress().getCity(),
                faculty.getAddress().getStreet(),
                faculty.getAddress().getPinCode(),
                faculty.getAddress().getState(),
                faculty.getDepartment().getDeptId()
        );

        Long generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        faculty.setFacultyId(generatedId);
        return faculty;
    }

    // Get All Faculty
    public List<Faculty> getAllFaculty() {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

        String query = """
            SELECT f.*, d.deptId, d.departmentName
            FROM Faculty f
            LEFT JOIN Department d ON f.departmentId = d.deptId
        """;

        return jdbcTemplate.query(query, (rs, rowNum) -> mapRowToFaculty(rs));
    }

    // Get Faculty By ID
    public Faculty getFacultyById(Long id) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

        String query = """
            SELECT f.*, d.deptId, d.departmentName
            FROM Faculty f
            LEFT JOIN Department d ON f.departmentId = d.deptId
            WHERE f.facultyId = ?
        """;

        return jdbcTemplate.queryForObject(query, new Object[]{id}, (rs, rowNum) -> mapRowToFaculty(rs));
    }

    // Update Faculty
    public Faculty updateFaculty(Long id, Faculty updatedFaculty) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();

        String updateQuery = """
            UPDATE Faculty SET name = ?, email = ?, contactNumber = ?, jobRole = ?,
            area = ?, city = ?, street = ?, pinCode = ?, state = ?, departmentId = ?
            WHERE facultyId = ?
        """;

        int rows = jdbcTemplate.update(updateQuery,
                updatedFaculty.getName(),
                updatedFaculty.getEmail(),
                updatedFaculty.getContactNumber(),
                updatedFaculty.getJobRole(),
                updatedFaculty.getAddress().getArea(),
                updatedFaculty.getAddress().getCity(),
                updatedFaculty.getAddress().getStreet(),
                updatedFaculty.getAddress().getPinCode(),
                updatedFaculty.getAddress().getState(),
                updatedFaculty.getDepartment().getDeptId(),
                id
        );

        if (rows == 0) throw new RuntimeException("Faculty with ID " + id + " not found.");

        updatedFaculty.setFacultyId(id);
        return updatedFaculty;
    }

    // Delete Faculty
    public void deleteFaculty(Long id) {
        JdbcTemplate jdbcTemplate = getJdbcTemplateForCurrentTenant();
        String deleteQuery = "DELETE FROM Faculty WHERE facultyId = ?";
        int rows = jdbcTemplate.update(deleteQuery, id);
        if (rows == 0) throw new RuntimeException("Faculty with ID " + id + " not found.");
    }

    // Helper method to map ResultSet to Faculty
    private Faculty mapRowToFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setFacultyId(rs.getLong("facultyId"));
        faculty.setName(rs.getString("name"));
        faculty.setEmail(rs.getString("email"));
        faculty.setContactNumber(rs.getString("contactNumber"));
        faculty.setJobRole(rs.getString("jobRole"));

        Address address = new Address();
        address.setArea(rs.getString("area"));
        address.setCity(rs.getString("city"));
        address.setStreet(rs.getString("street"));
        address.setPinCode(rs.getString("pinCode"));
        address.setState(rs.getString("state"));
        faculty.setAddress(address);

        Department department = new Department();
        department.setDeptId(rs.getLong("departmentId"));
        department.setDepartmentName(rs.getString("departmentName"));
        faculty.setDepartment(department);

        return faculty;
    }
}
