package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.User;
import com.rev.app.service.UserService;
import com.rev.app.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/team")
public class ManagerTeamController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewTeam(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    List<EmployeeDto> team = employeeService.getDirectReports(manager.getEmpId());
                    model.addAttribute("team", team);
                    model.addAttribute("manager", manager);
                }
            }
        }
        return "manager/team/index";
    }

    @GetMapping("/profile/{id}")
    public String viewMemberProfile(@PathVariable String id, Model model, Principal principal) {
        // In a real app, verify manager has permission for this profile
        EmployeeDto employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return "redirect:/manager/team?error=notfound";
        }
        
        model.addAttribute("employee", employee);
        return "manager/team/profile";
    }
}
