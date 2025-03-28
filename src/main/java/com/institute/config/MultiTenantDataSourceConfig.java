package com.institute.config;


import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

@ConditionalOnMissingBean(DataSource.class)
public class MultiTenantDataSourceConfig {
@Value("${spring.datasource.username}")
private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.configurl}")
    private String configurl;
    @Value("${spring.datasource.driver-class-name}")
    private String driverclassname;
    private final Map<String, DataSource> tenantDataSources = new HashMap<>();

    public DataSource resolveDataSource(String dbName) {
        return tenantDataSources.computeIfAbsent(dbName, this::createDataSource);
    }

    private DataSource createDataSource(String dbName) {
        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + dbName);
        dataSource.setJdbcUrl(configurl+dbName);
        dataSource.setUsername(password);
        dataSource.setPassword(username);
        dataSource.setDriverClassName(driverclassname);
        return dataSource;
    }
}


//package com.institute.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class MultiTenantDataSourceConfig {
//
//    private final Map<String, DataSource> tenantDataSources = new HashMap<>();
//
//    public DataSource resolveDataSource(String dbName) {
//        return tenantDataSources.computeIfAbsent(dbName, this::createDataSource);
//    }
//
//    private DataSource createDataSource(String dbName) {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + dbName);
//        dataSource.setUsername("root");
//        dataSource.setPassword("Nikhil@0114");
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        return dataSource;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        MultiTenantDataSource multiTenantDataSource = new MultiTenantDataSource();
//        multiTenantDataSource.setTargetDataSources(new HashMap<>()); // Empty initially
//        return multiTenantDataSource;
//    }
//}

//
//package com.institute.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableTransactionManagement
//public class MultiTenantDataSourceConfig {
//
//    private final Map<String, DataSource> tenantDataSources = new HashMap<>();
//
//    public DataSource resolveDataSource(String dbName) {
//        return tenantDataSources.computeIfAbsent(dbName, this::createDataSource);
//    }
//
//    private DataSource createDataSource(String dbName) {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + dbName);
//        dataSource.setUsername("root");
//        dataSource.setPassword("Nikhil@0114");
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        return dataSource;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        MultiTenantDataSource multiTenantDataSource = new MultiTenantDataSource();
//        multiTenantDataSource.setTargetDataSources(new HashMap<>()); // Initially empty
//        multiTenantDataSource.afterPropertiesSet(); // Ensures proper initialization
//        return multiTenantDataSource;
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            EntityManagerFactoryBuilder builder, @Autowired DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("com.institute.model") // Make sure all entities are inside this package
//                .persistenceUnit("multiTenantPU")
//                .properties(getJpaProperties())
//                .build();
//    }
//
//    private Map<String, Object> getJpaProperties() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
//        properties.put("hibernate.hbm2ddl.auto", "update"); // Change as needed
//        return properties;
//    }
//}
