package com.rev.app.controller;

import com.rev.app.dto.LeaveDto;
import com.rev.app.entity.User;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.HolidayService;
import com.rev.app.service.LeaveService;
import com.rev.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String myLeaves(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                com.rev.app.dto.EmployeeDto empDto = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (empDto != null) {
                    model.addAttribute("leaves", leaveService.getEmployeeLeaves(empDto.getEmpId()));
                    model.addAttribute("balances", leaveService.getLeaveBalances(empDto.getEmpId()));
                }
            }
        }
        return "leaves/my-leaves";
    }

    @GetMapping("/apply")
    public String applyLeaveForm(Model model) {
        model.addAttribute("leave", new LeaveDto());
        model.addAttribute("leaveTypes", leaveService.getAllLeaveTypes());
        return "leaves/apply-form";
    }

    @PostMapping("/save")
    public String saveLeave(@ModelAttribute LeaveDto dto, Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                com.rev.app.dto.EmployeeDto empDto = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (empDto != null) {
                    dto.setEmpId(empDto.getEmpId());
                    try {
                        leaveService.applyLeave(dto);
                    } catch (com.rev.app.exceptions.BusinessException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                        return "redirect:/leaves/apply";
                    }
                }
            }
        }
        return "redirect:/leaves";
    }
    
    @PostMapping("/{id}/cancel")
    public String cancelLeave(@PathVariable Long id) {
        leaveService.cancelLeave(id);
        return "redirect:/leaves";
    }

    @GetMapping("/holidays")
    public String holidayCalendar(Model model) {
        model.addAttribute("holidays", holidayService.getAllHolidays());
        return "leaves/holidays";
    }
}

