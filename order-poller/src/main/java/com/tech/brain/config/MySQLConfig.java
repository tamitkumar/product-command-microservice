package com.tech.brain.config;

import com.tech.brain.exception.ErrorCode;
import com.tech.brain.exception.ErrorSeverity;
import com.tech.brain.exception.PollerException;
import com.tech.brain.utils.PollerConstants;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tech.brain.repository", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "platformTransactionManager")
public class MySQLConfig {
    @Bean
    DataSource dataSource() {
        String dbUser = PollerConstants.DB_USER_NAME;
        String dbPassword = PollerConstants.DB_PASSWORD;
        String driverClassName = PollerConstants.DB_DRIVER_CLASS_NAME;
        DriverManagerDataSource ds = new DriverManagerDataSource(getDBUrl(), dbUser, dbPassword);
        try {
            ds.setDriverClassName(driverClassName);
        } catch (Exception e) {
            throw new PollerException(ErrorCode.ERR010.getErrorCode(), ErrorSeverity.FATAL,
                    ErrorCode.ERR010.getErrorMessage(), e);
        }
        try {
            ds.getConnection().close();
        } catch (SQLException e) {
            throw new PollerException(e);
        }
        return ds;
    }

    private String getDBUrl() {
        String dbHost = PollerConstants.DB_HOST;
        String dbPort = PollerConstants.DB_PORT;
        String dbName = PollerConstants.DB_NAME;
        String dbUrlPrefix = PollerConstants.DB_URL_PREFIX;
        //		baseUrl.append(EMPConstant.COLON);
        return dbUrlPrefix + dbHost +
                PollerConstants.COLON +
                dbPort +
                dbName;
    }

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    PlatformTransactionManager platformTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf);
        return txManager;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("com.tech.brain.entity");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaProperties(jpaProperties());
        return factoryBean;
    }

    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put(PollerConstants.DIALECT_KEY, PollerConstants.DIALECT_VALUE);
        properties.put("hibernate.dialect", "com.tech.brain.config.DialectConfig");
        properties.put(PollerConstants.SHOW_SQL_KEY, PollerConstants.SHOW_SQL_VALUE);
        properties.put(PollerConstants.FORMAT_SQL_KEY, PollerConstants.FORMAT_SQL_VALUE);
        properties.put("spring.jpa.hibernate.ddl-auto", "update");
        return properties;
    }
}
