package com.farmstock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Slf4j
public class DatabaseConfig {
    
    @Value("${PGHOST:}")
    private String host;
    
    @Value("${PGPORT:}")
    private String port;
    
    @Value("${PGDATABASE:}")
    private String database;
    
    @Value("${PGUSER:}")
    private String username;
    
    @Value("${PGPASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        String dbUrl = System.getenv("DATABASE_URL");
        
        log.info("=========================================");
        log.info("Database Configuration:");
        log.info("DATABASE_URL present: {}", dbUrl != null && !dbUrl.isEmpty());
        if (dbUrl != null && !dbUrl.isEmpty()) {
            // Log partial URL for debugging (hide password)
            String safeUrl = dbUrl.contains("@") 
                ? dbUrl.substring(0, dbUrl.indexOf("@")) + "@***" 
                : dbUrl;
            log.info("DATABASE_URL: {}", safeUrl);
        }
        log.info("=========================================");
        
        if (dbUrl != null && !dbUrl.isEmpty()) {
            // Parse DATABASE_URL from Railway format: postgresql://user:password@host:port/database
            try {
                URI uri = new URI(dbUrl);
                String parsedUsername = "";
                String parsedPassword = "";
                
                // Parse user:password from userInfo
                if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty()) {
                    String userInfo = uri.getUserInfo();
                    int colonIndex = userInfo.indexOf(':');
                    if (colonIndex >= 0) {
                        parsedUsername = userInfo.substring(0, colonIndex);
                        parsedPassword = userInfo.substring(colonIndex + 1);
                    } else {
                        parsedUsername = userInfo;
                    }
                }
                
                String parsedHost = uri.getHost();
                int parsedPort = uri.getPort() != -1 ? uri.getPort() : 5432;
                String parsedDatabase = uri.getPath().startsWith("/") ? uri.getPath().substring(1) : uri.getPath();
                
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", parsedHost, parsedPort, parsedDatabase);
                
                log.info("Parsed database connection:");
                log.info("  Host: {}", parsedHost);
                log.info("  Port: {}", parsedPort);
                log.info("  Database: {}", parsedDatabase);
                log.info("  Username: {}", parsedUsername);
                log.info("  JDBC URL: jdbc:postgresql://{}:{}/{}", parsedHost, parsedPort, parsedDatabase);
                
                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .username(parsedUsername)
                        .password(parsedPassword)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL: {}", dbUrl, e);
                throw new RuntimeException("Failed to parse DATABASE_URL: " + dbUrl, e);
            }
        } else {
            // Fall back to individual environment variables
            log.info("Using individual environment variables:");
            log.info("  PGHOST: {}", host);
            log.info("  PGPORT: {}", port);
            log.info("  PGDATABASE: {}", database);
            log.info("  PGUSER: {}", username);
            
            if (host == null || host.isEmpty() || 
                port == null || port.isEmpty() || 
                database == null || database.isEmpty()) {
                log.error("Neither DATABASE_URL nor PGHOST/PGPORT/PGDATABASE are set!");
                throw new RuntimeException("Either DATABASE_URL or PGHOST/PGPORT/PGDATABASE must be set");
            }
            
            String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
            log.info("JDBC URL: {}", url);
            
            return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
    }
}