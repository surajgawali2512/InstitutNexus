package com.institute.config;


import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenantDataSourceConfig {

    private final Map<String, DataSource> tenantDataSources = new HashMap<>();

    public DataSource resolveDataSource(String dbName) {
        return tenantDataSources.computeIfAbsent(dbName, this::createDataSource);
    }

    private DataSource createDataSource(String dbName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + dbName);
        dataSource.setUsername("root");
        dataSource.setPassword("Nikhil@0114");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
}
