package com.rev.app.config;

import com.rev.app.entity.Department;
import com.rev.app.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDepartments(DepartmentRepository departmentRepository) {
        return args -> {
            if (departmentRepository.count() == 0) {
                departmentRepository.saveAll(List.of(
                    new Department(null, "IT"),
                    new Department(null, "HR"),
                    new Department(null, "Finance"),
                    new Department(null, "Marketing"),
                    new Department(null, "Operations")
                ));
                System.out.println("[DataInitializer] Seeded default departments.");
            }
        };
    }
}
