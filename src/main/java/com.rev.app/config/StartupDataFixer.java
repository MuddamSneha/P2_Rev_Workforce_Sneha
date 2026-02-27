package com.rev.app.config;

import com.rev.app.entity.Employee;
import com.rev.app.entity.Notification;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.NotificationRepository;
import com.rev.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Runs once at startup to ensure all employees have a manager assigned.
 * This fixes cases where employees were registered via the registration form
 * without selecting a manager.
 */
@Component
public class StartupDataFixer {

    private static final Logger log = LoggerFactory.getLogger(StartupDataFixer.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onStartup() {
        fixManagerAssignments();
        createSeedNotifications();
    }

    public void fixManagerAssignments() {
        log.info("StartupDataFixer: checking and fixing manager assignments...");
        // ... (existing logic remains)
        List<Employee> allEmployees = employeeRepository.findAll();
        // ...
        Employee defaultManager = allEmployees.stream()
                .filter(e -> e.getUser() != null
                        && e.getUser().getRole().name().equals("ROLE_MANAGER"))
                .findFirst().orElse(null);

        Employee defaultAdmin = allEmployees.stream()
                .filter(e -> e.getUser() != null
                        && e.getUser().getRole().name().equals("ROLE_ADMIN"))
                .findFirst().orElse(null);

        int fixed = 0;
        for (Employee emp : allEmployees) {
            if (emp.getManager() == null && emp.getUser() != null) {
                String role = emp.getUser().getRole().name();
                Employee assigned = null;

                if ("ROLE_EMPLOYEE".equals(role) && defaultManager != null
                        && !emp.getEmpId().equals(defaultManager.getEmpId())) {
                    assigned = defaultManager;
                } else if ("ROLE_MANAGER".equals(role) && defaultAdmin != null
                        && !emp.getEmpId().equals(defaultAdmin.getEmpId())) {
                    assigned = defaultAdmin;
                }

                if (assigned != null) {
                    emp.setManager(assigned);
                    employeeRepository.save(emp);
                    log.info("StartupDataFixer: assigned {} ({}) → manager {}",
                            emp.getEmpId(), emp.getUser().getEmail(), assigned.getEmpId());
                    fixed++;
                }
            }
        }

        if (fixed == 0) {
            log.info("StartupDataFixer: all employees already have managers. No changes needed.");
        } else {
            log.info("StartupDataFixer: fixed {} employee(s) with missing manager assignments.", fixed);
        }
    }

    private void createSeedNotifications() {
        log.info("StartupDataFixer: checking for missing seed notifications...");
        List<com.rev.app.entity.User> allUsers = userRepository.findAll();
        int createdCount = 0;
        
        for (com.rev.app.entity.User user : allUsers) {
            long count = notificationRepository.countByUser_UserId(user.getUserId());
            if (count == 0) {
                notificationRepository.save(new Notification(
                        null,
                        user,
                        "Welcome to RevWorkforce! Your dashboard is now active.",
                        "SYSTEM",
                        0,
                        null
                ));
                
                // If they are a manager, add a mocked leave request
                if ("ROLE_MANAGER".equals(user.getRole().name())) {
                    notificationRepository.save(new Notification(
                            null,
                            user,
                            "New pending leave request from team member.",
                            "LEAVE_REQUEST",
                            0,
                            null
                    ));
                }
                createdCount++;
            }
        }
        
        if (createdCount > 0) {
            log.info("StartupDataFixer: created seed notifications for {} users.", createdCount);
        }
    }
}
