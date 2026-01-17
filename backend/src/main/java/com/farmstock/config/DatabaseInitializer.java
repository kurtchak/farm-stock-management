package com.farmstock.config;

import com.farmstock.model.User;
import com.farmstock.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Update admin user password
            userRepository.findByUsername("admin").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("password"));
                userRepository.save(admin);
                System.out.println("Updated admin password");
            });

            // Update worker1 user password
            userRepository.findByUsername("worker1").ifPresent(worker -> {
                worker.setPassword(passwordEncoder.encode("password"));
                userRepository.save(worker);
                System.out.println("Updated worker1 password");
            });
        };
    }
}
