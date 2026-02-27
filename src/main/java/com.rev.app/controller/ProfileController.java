package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.User;
import com.rev.app.service.UserService;
import com.rev.app.service.EmployeeService;
import com.rev.app.exceptions.ResourceNotFoundException;
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
                EmployeeDto currentEmp = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (currentEmp != null) {
                    employeeService.updateProfile(currentEmp.getEmpId(), dto);
                }
            }
        }
        return "redirect:/profile?success";
    }
}
