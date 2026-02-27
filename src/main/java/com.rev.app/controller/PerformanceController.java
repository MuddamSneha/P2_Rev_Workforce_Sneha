package com.rev.app.controller;

import com.rev.app.dto.GoalDto;
import com.rev.app.dto.PerformanceReviewDto;
import com.rev.app.entity.User;
import com.rev.app.service.UserService;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.GoalService;
import com.rev.app.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private GoalService goalService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String performanceDashboard(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                com.rev.app.dto.EmployeeDto empDto = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (empDto != null) {
                    String empId = empDto.getEmpId();
                    model.addAttribute("goals", goalService.getEmployeeGoals(empId));
                    model.addAttribute("reviews", performanceService.getEmployeeReviews(empId));
                }
            }
        }
        return "performance/index";
    }

    @GetMapping("/review/submit")
    public String submitReviewForm(Model model) {
        model.addAttribute("review", new PerformanceReviewDto());
        return "performance/submit-review";
    }

    @PostMapping("/review/save")
    public String saveReview(@ModelAttribute PerformanceReviewDto dto, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                com.rev.app.dto.EmployeeDto empDto = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (empDto != null) {
                    dto.setEmpId(empDto.getEmpId());
                    dto.setReviewYear(java.time.Year.now().getValue());
                    performanceService.submitReview(dto);
                }
            }
        }
        return "redirect:/performance";
    }

    @GetMapping("/goal/add")
    public String addGoalForm(Model model) {
        model.addAttribute("goal", new GoalDto());
        return "performance/add-goal";
    }

    @PostMapping("/goal/save")
    public String saveGoal(@ModelAttribute GoalDto dto, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                com.rev.app.dto.EmployeeDto empDto = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (empDto != null) {
                    dto.setEmpId(empDto.getEmpId());
                    goalService.createGoal(dto);
                }
            }
        }
        return "redirect:/performance";
    }

    @PostMapping("/goal/update")
    public String updateGoal(@RequestParam Long goalId, @RequestParam String status, @RequestParam Integer progress) {
        goalService.updateGoalStatus(goalId, status, progress);
        return "redirect:/performance";
    }
}
