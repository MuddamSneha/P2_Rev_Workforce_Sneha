package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/notifications")
    public String notifications(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                model.addAttribute("notifications", notificationService.getUserNotifications(userOpt.get().getUserId()));
            }
        }
        return "notifications";
    }

    @PostMapping("/notifications/read/{id}")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }
}
