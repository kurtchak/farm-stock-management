package com.farmstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.farmstock.repository")
@EnableAsync
public class FarmStockApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmStockApplication.class, args);
    }
}