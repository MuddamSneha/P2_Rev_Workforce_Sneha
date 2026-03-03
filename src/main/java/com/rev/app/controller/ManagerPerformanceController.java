package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.dto.PerformanceReviewDto;
import com.rev.app.entity.User;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.GoalService;
import com.rev.app.service.PerformanceService;
import com.rev.app.service.UserService;
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
    public String viewTeamPerformance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewId") String sortBy,
            Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                EmployeeDto manager = employeeService.getEmployeeByUserId(userOpt.get().getUserId());
                if (manager != null) {
                    org.springframework.data.domain.Page<PerformanceReviewDto> teamReviewsPage = performanceService.getTeamReviews(manager.getEmpId(), page, size, sortBy);
                    List<EmployeeDto> team = employeeService.getDirectReports(manager.getEmpId());
                    
                    model.addAttribute("teamReviews", teamReviewsPage.getContent());
                    model.addAttribute("page", teamReviewsPage);
                    model.addAttribute("team", team);
                }
            }
        }
        return "manager/performance/index";
    }

    @GetMapping("/review/{id}")
    public String showReviewForm(@PathVariable Long id, Model model) {
        // In a real app, verify manager has permission for this review
        PerformanceReviewDto review = performanceService.getReviewById(id);
        
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
    public String viewEmployeeGoals(
            @PathVariable String empId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "goalId") String sortBy,
            Model model) {
        org.springframework.data.domain.Page<com.rev.app.dto.GoalDto> goalsPage = goalService.getEmployeeGoals(empId, page, size, sortBy);
        EmployeeDto employee = employeeService.getEmployeeById(empId);
        
        model.addAttribute("goals", goalsPage.getContent());
        model.addAttribute("page", goalsPage);
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

    @PostMapping("/quick-rate")
    public String quickRate(@RequestParam String empId, 
                            @RequestParam BigDecimal rating, 
                            @RequestParam String comment) {
        performanceService.submitQuickRating(empId, rating, comment);
        return "redirect:/dashboard?success=rated";
    }
}
