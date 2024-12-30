package com.farmstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.farmstock.model"})
@EnableJpaRepositories(basePackages = {"com.farmstock.repository"})
public class FarmStockApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmStockApplication.class, args);
    }
}