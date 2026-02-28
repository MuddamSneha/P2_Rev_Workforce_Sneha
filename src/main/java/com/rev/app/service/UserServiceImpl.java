package com.rev.app.service;

import com.rev.app.dto.RegistrationDto;
import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.entity.User;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Override
    @Transactional
    public User createUser(String email, String password, User.Role role) {
        User user = new User(
                null,
                email,
                passwordEncoder.encode(password),
                role,
                1,
                null,
                null
        );
        return userRepository.save(user);
    }

    @Autowired
    private LeaveService leaveService;

    @Override
    @Transactional
    public User registerUser(RegistrationDto dto) {
        User user = new User(
                null,
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                User.Role.valueOf(dto.getRole()),
                1,
                null,
                null
        );
        User savedUser = userRepository.save(user);

        Department dept = null;
        if (dto.getDepartmentId() != null) {
            dept = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
        }

        Designation designation = null;
        if (dto.getDesignationId() != null) {
            designation = designationRepository.findById(dto.getDesignationId()).orElse(null);
        }

        Employee manager = null;
        if (dto.getManagerId() != null && !dto.getManagerId().isEmpty()) {
            manager = employeeRepository.findById(dto.getManagerId()).orElse(null);
        }

        // If no manager was selected, auto-assign a default manager
        if (manager == null) {
            User.Role userRole = User.Role.valueOf(dto.getRole());
            if (userRole == User.Role.ROLE_EMPLOYEE) {
                // Assign first available ROLE_MANAGER as default
                manager = employeeRepository.findAll().stream()
                        .filter(e -> e.getUser() != null && e.getUser().getRole() == User.Role.ROLE_MANAGER)
                        .findFirst().orElse(null);
            } else if (userRole == User.Role.ROLE_MANAGER) {
                // Assign first available ROLE_ADMIN as default
                manager = employeeRepository.findAll().stream()
                        .filter(e -> e.getUser() != null && e.getUser().getRole() == User.Role.ROLE_ADMIN)
                        .findFirst().orElse(null);
            }
        }

        Employee employee = new Employee(
                "RW" + String.format("%03d", (int) (Math.random() * 1000)), // Simple random ID for now
                savedUser,
                dto.getFirstName(),
                dto.getLastName(),
                null,
                null,
                null,
                null,
                LocalDate.now(),
                dept,
                designation,
                manager,
                null,
                null
        );
        employeeRepository.save(employee);
        leaveService.initializeLeaveBalances(employee.getEmpId());

        return savedUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void setActiveStatus(Long userId, boolean isActive) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsActive(isActive ? 1 : 0);
            userRepository.save(user);
        });
    }
}
