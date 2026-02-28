package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.dto.LeaveDto;
import com.rev.app.entity.User;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.LeaveService;
import com.rev.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/manager/leaves")
public class ManagerLeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewTeamLeaves(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    List<EmployeeDto> team = employeeService.getDirectReports(manager.getEmpId());
                    List<LeaveDto> pendingLeaves = leaveService.getPendingLeavesForManager(manager.getEmpId());
                    List<com.rev.app.entity.LeaveType> leaveTypes = leaveService.getAllLeaveTypes();
                    
                    Map<String, Map<Long, Integer>> teamBalancesMap = new HashMap<>();
                    for (EmployeeDto emp : team) {
                        List<com.rev.app.entity.LeaveBalance> balances = leaveService.getLeaveBalances(emp.getEmpId());
                        Map<Long, Integer> balanceByTypeId = new HashMap<>();
                        for (com.rev.app.entity.LeaveBalance b : balances) {
                            balanceByTypeId.put(b.getLeaveType().getLeaveTypeId(), b.getBalanceDays());
                        }
                        teamBalancesMap.put(emp.getEmpId(), balanceByTypeId);
                    }

                    model.addAttribute("team", team);
                    model.addAttribute("pendingLeaves", pendingLeaves);
                    model.addAttribute("teamBalancesMap", teamBalancesMap);
                    model.addAttribute("leaveTypes", leaveTypes);
                }
            }
        }
        return "manager/team-leaves";
    }

    @PostMapping("/approve")
    public String approveLeave(@RequestParam Long leaveId, @RequestParam(required = false) String comment, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    leaveService.approveLeave(leaveId, manager.getEmpId(), comment);
                }
            }
        }
        return "redirect:/manager/leaves?success=approved";
    }

    @PostMapping("/reject")
    public String rejectLeave(@RequestParam Long leaveId, @RequestParam String comment, Principal principal) {
        if (comment == null || comment.trim().isEmpty()) {
            return "redirect:/manager/leaves?error=comment_required";
        }
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    leaveService.rejectLeave(leaveId, manager.getEmpId(), comment);
                }
            }
        }
        return "redirect:/manager/leaves?success=rejected";
    }

    @GetMapping("/calendar")
    public String viewTeamCalendar(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    List<LeaveDto> teamLeaves = leaveService.getTeamLeaves(manager.getEmpId());
                    model.addAttribute("teamLeaves", teamLeaves);
                }
            }
        }
        return "manager/team-calendar";
    }
}
