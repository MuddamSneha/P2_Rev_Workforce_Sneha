package com.rev.app.controller;

import com.rev.app.dto.RegistrationDto;
import com.rev.app.entity.User;
import com.rev.app.service.ConfigService;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        model.addAttribute("departments", configService.getAllDepartments());
        model.addAttribute("designations", configService.getAllDesignations());
        model.addAttribute("managers", employeeService.getAllEmployees());
        model.addAttribute("roles", User.Role.values());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto, 
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", configService.getAllDepartments());
            model.addAttribute("designations", configService.getAllDesignations());
            model.addAttribute("managers", employeeService.getAllEmployees());
            model.addAttribute("roles", User.Role.values());
            return "register";
        }
        userService.registerUser(registrationDto);
        return "redirect:/login?registered";
    }
}
