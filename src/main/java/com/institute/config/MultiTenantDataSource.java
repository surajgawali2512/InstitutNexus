package com.institute.config;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MultiTenantDataSource extends AbstractRoutingDataSource {

    @Autowired
    private MultiTenantDataSourceConfig dataSourceConfig;

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenant = (String) determineCurrentLookupKey();
        if (tenant == null) {
            throw new IllegalStateException("No tenant found!");
        }
        return dataSourceConfig.resolveDataSource(tenant);
    }
}
