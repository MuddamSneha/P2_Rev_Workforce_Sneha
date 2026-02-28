package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.User;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                try {
                    EmployeeDto employee = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                    model.addAttribute("employee", employee);
                } catch (ResourceNotFoundException e) {
                    model.addAttribute("employee", null);
                }
            }
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("employee") EmployeeDto dto, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto currentEmp = null;
                try {
                    currentEmp = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                } catch (Exception e) {
                    // Handle missing employee profile gracefully
                }
                if (currentEmp != null) {
                    employeeService.updateProfile(currentEmp.getEmpId(), dto);
                }
            }
        }
        return "redirect:/profile?success";
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
                    return "redirect:/profile";
                }
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
                    return "redirect:/profile";
                }
                userService.updatePassword(user.getUserId(), newPassword);
                redirectAttributes.addFlashAttribute("successPwd", "Password changed successfully.");
            }
        }
        return "redirect:/profile";
    }
}
