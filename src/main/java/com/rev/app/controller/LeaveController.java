package com.rev.app.controller;

import com.rev.app.dto.LeaveDto;
import com.rev.app.entity.HolidayCalendar;
import com.rev.app.entity.User;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.HolidayService;
import com.rev.app.service.LeaveService;
import com.rev.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public String myLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "leaveId") String sortBy,
            Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                com.rev.app.dto.EmployeeDto empDto = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (empDto != null) {
                    org.springframework.data.domain.Page<LeaveDto> leavePage = leaveService.getEmployeeLeaves(empDto.getEmpId(), page, size, sortBy);
                    model.addAttribute("leaves", leavePage.getContent());
                    model.addAttribute("page", leavePage);
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
        model.addAttribute("newHoliday", new HolidayCalendar());
        return "leaves/holidays";
    }

    /**
     * Admin-only: saves a new holiday and redirects back to the shared calendar
     * so all users (including admin) immediately see the updated list.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/holidays/save")
    public String saveHolidayFromCalendar(@ModelAttribute HolidayCalendar holiday) {
        holidayService.saveHoliday(holiday);
        return "redirect:/leaves/holidays";
    }

    /**
     * Admin-only: deletes a holiday and redirects back to the shared calendar.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/holidays/delete/{id}")
    public String deleteHolidayFromCalendar(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return "redirect:/leaves/holidays";
    }
}

