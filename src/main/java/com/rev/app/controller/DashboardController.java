package com.rev.app.controller;

import com.rev.app.dto.NotificationDto;
import com.rev.app.entity.User;
import com.rev.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        // For now, we assume email is the principal name
        // We need a way to get the employee details
        // I'll implement a custom UserDetailsService later to make this cleaner

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<NotificationDto> notifications = notificationService.getUserNotifications(user.getUserId());
            model.addAttribute("notifications", notifications);
            
            if (user.getRole() == com.rev.app.entity.User.Role.ROLE_ADMIN) {
                model.addAttribute("metrics", reportService.getDashboardMetrics());
            }
        }

        return "dashboard";
    }
}
