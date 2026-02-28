package com.rev.app.exceptions;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ModelAttribute("userEmployee")
    public EmployeeDto addUserDetailsToModel(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                try {
                    return employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                } catch (ResourceNotFoundException e) {
                    log.warn("Employee record not found for user: {}", email);
                    return null;
                }
            }
        }
        return null;
    }

    @ModelAttribute("notifications")
    public java.util.List<com.rev.app.dto.NotificationDto> addNotificationsToModel(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return notificationService.getUserNotifications(userOpt.get().getUserId());
            }
        }
        return java.util.Collections.emptyList();
    }

    @ModelAttribute("notificationCount")
    public Long addNotificationCountToModel(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return notificationService.getUserNotifications(userOpt.get().getUserId())
                        .stream().filter(n -> n.getIsRead() == 0).count();
            }
        }
        return 0L;
    }
}
