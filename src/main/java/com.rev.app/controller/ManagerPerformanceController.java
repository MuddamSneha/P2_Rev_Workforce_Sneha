package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.dto.PerformanceReviewDto;
import com.rev.app.entity.Goal;
import com.rev.app.entity.User;
import com.rev.app.service.GoalService;
import com.rev.app.service.UserService;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/performance")
public class ManagerPerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private GoalService goalService;

    @GetMapping
    public String viewTeamPerformance(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    List<PerformanceReviewDto> teamReviews = performanceService.getTeamReviews(manager.getEmpId());
                    List<EmployeeDto> team = employeeService.getDirectReports(manager.getEmpId());
                    
                    model.addAttribute("teamReviews", teamReviews);
                    model.addAttribute("team", team);
                }
            }
        }
        return "manager/performance/index";
    }

    @GetMapping("/review/{id}")
    public String showReviewForm(@PathVariable Long id, Model model) {
        // In a real app, verify manager has permission for this review
        PerformanceReviewDto review = performanceService.getEmployeeReviews(null).stream() // Utility to find by id would be better
                .filter(r -> r.getReviewId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        model.addAttribute("review", review);
        return "manager/performance/review-form";
    }

    @PostMapping("/review")
    public String submitFeedback(@RequestParam Long reviewId, 
                                 @RequestParam BigDecimal rating, 
                                 @RequestParam String feedback) {
        performanceService.provideFeedback(reviewId, rating, feedback);
        return "redirect:/manager/performance?success=reviewed";
    }

    @GetMapping("/goals/{empId}")
    public String viewEmployeeGoals(@PathVariable String empId, Model model) {
        List<com.rev.app.dto.GoalDto> goals = goalService.getEmployeeGoals(empId);
        EmployeeDto employee = employeeService.getEmployeeById(empId);
        
        model.addAttribute("goals", goals);
        model.addAttribute("employee", employee);
        return "manager/performance/team-goals";
    }

    @PostMapping("/goals/comment")
    public String submitGoalComment(@RequestParam Long goalId, 
                                    @RequestParam String empId,
                                    @RequestParam String comment) {
        performanceService.reviewGoal(goalId, comment);
        return "redirect:/manager/performance/goals/" + empId + "?success=commented";
    }
}
