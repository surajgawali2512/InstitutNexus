//package com.institute.config;
//
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import javax.sql.DataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MultiTenantDataSource extends AbstractRoutingDataSource {
//
//    @Autowired
//    private MultiTenantDataSourceConfig dataSourceConfig;
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TenantContext.getCurrentTenant();
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//        String tenant = (String) determineCurrentLookupKey();
//        if (tenant == null) {
//            throw new IllegalStateException("No tenant found!");
//        }
//        return dataSourceConfig.resolveDataSource(tenant);
//    }git
//}
//
package com.institute.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
@ConditionalOnMissingBean(DataSource.class)
public class MultiTenantDataSource extends AbstractRoutingDataSource {

    private final MultiTenantDataSourceConfig dataSourceConfig;

    public MultiTenantDataSource(MultiTenantDataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

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
//
//package com.institute.config;
//
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import javax.sql.DataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class MultiTenantDataSource extends AbstractRoutingDataSource {
//
//    @Autowired
//    private MultiTenantDataSourceConfig dataSourceConfig;
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TenantContext.getCurrentTenant();
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//        String tenant = (String) determineCurrentLookupKey();
//        if (tenant == null) {
//            throw new IllegalStateException("No tenant found!");
//        }
//        return dataSourceConfig.resolveDataSource(tenant);
//    }
//}




//
//package com.institute.config;
//
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import javax.sql.DataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MultiTenantDataSource extends AbstractRoutingDataSource {
//
//    @Autowired
//    private MultiTenantDataSourceConfig dataSourceConfig;
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return TenantContext.getCurrentTenant();
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//        String tenant = (String) determineCurrentLookupKey();
//        if (tenant == null) {
//            throw new IllegalStateException("No tenant found!");
//        }
//        return dataSourceConfig.resolveDataSource(tenant);
//    }
//}
