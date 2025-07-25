package com.farmstock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    @Value("${PGHOST}")
    private String host;
    
    @Value("${PGPORT}")
    private String port;
    
    @Value("${PGDATABASE}")
    private String database;
    
    @Value("${PGUSER}")
    private String username;
    
    @Value("${PGPASSWORD}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}