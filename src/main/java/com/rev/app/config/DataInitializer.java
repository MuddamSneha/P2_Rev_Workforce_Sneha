package com.rev.app.config;

import com.rev.app.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner logUsers(com.rev.app.repository.UserRepository userRepository) {
        return args -> {
            System.out.println("--- Current Users in Database ---");
            userRepository.findAll().forEach(u -> 
                System.out.println("User: " + u.getEmail() + ", Role: " + u.getRole())
            );
            
            // Force specific user to be ADMIN for debugging
            userRepository.findByEmailIgnoreCase("muddam1234@gmail.com").ifPresent(u -> {
                if (u.getRole() != User.Role.ROLE_ADMIN) {
                    u.setRole(User.Role.ROLE_ADMIN);
                    userRepository.save(u);
                    System.out.println("[DataInitializer] Updated muddam1234@gmail.com to ROLE_ADMIN");
                }
            });
            
            // Ensure admin@rev.com exists
            if (userRepository.findByEmailIgnoreCase("admin@rev.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@rev.com");
                admin.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8q7uy5AkNIvH6VTu2lO/k.3.y8uK07N9Dbm"); // 'password'
                admin.setRole(User.Role.ROLE_ADMIN);
                admin.setIsActive(1);
                userRepository.save(admin);
                System.out.println("[DataInitializer] Created default admin@rev.com");
            }
        };
    }
}
