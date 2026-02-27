package com.rev.app.rest;

import com.rev.app.dto.NotificationDto;
import com.rev.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.rev.app.service.UserService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    public List<NotificationDto> getNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @GetMapping("/all")
    public List<NotificationDto> getAll() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/me")
    public java.util.Map<String, Object> getCurrentUser(java.security.Principal principal) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        if (principal != null) {
            String name = principal.getName();
            map.put("principalName", name);
            userService.findByEmail(name).ifPresentOrElse(user -> {
                map.put("userId", user.getUserId());
                map.put("email", user.getEmail());
                map.put("role", user.getRole());
                map.put("notifications", notificationService.getUserNotifications(user.getUserId()));
            }, () -> {
                map.put("error", "No user found in DB for principal: " + name);
            });
        } else {
            map.put("error", "Principal is null (not logged in)");
        }
        return map;
    }
}
