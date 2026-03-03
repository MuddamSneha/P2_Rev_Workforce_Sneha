package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.dto.LeaveDto;
import com.rev.app.dto.NotificationDto;
import com.rev.app.dto.PerformanceReviewDto;
import com.rev.app.entity.User;
import com.rev.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private com.rev.app.service.PerformanceService performanceService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login/admin")
    public String loginAdmin(Model model) {
        model.addAttribute("portalRole", "ROLE_ADMIN");
        return "login-admin";
    }

    @GetMapping("/login/manager")
    public String loginManager(Model model) {
        model.addAttribute("portalRole", "ROLE_MANAGER");
        return "login-manager";
    }

    @GetMapping("/login/employee")
    public String loginEmployee(Model model) {
        model.addAttribute("portalRole", "ROLE_EMPLOYEE");
        return "login-employee";
    }

    @GetMapping("/login")
    public String login(@org.springframework.web.bind.annotation.RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            return "redirect:/?error";
        }
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        // For now, we assume email is the principal name
        // We need a way to get the employee details
        // I'll implement a custom UserDetailsService later to make this cleaner

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            EmployeeDto employee = null;
            try {
                employee = employeeService.getEmployeeByUserId(user.getUserId());
            } catch (Exception e) {
                // If no employee profile exists (e.g., for Admin), continue with null
            }
            model.addAttribute("userEmployee", employee);
            
            List<NotificationDto> notifications = notificationService.getUserNotifications(user.getUserId());
            model.addAttribute("notifications", notifications);
            model.addAttribute("announcements", announcementService.getAllAnnouncements(0, 5).getContent());

            if (user.getRole() == User.Role.ROLE_ADMIN) {
                model.addAttribute("metrics", reportService.getDashboardMetrics());
                return "admin/dashboard";
            } else if (user.getRole() == User.Role.ROLE_MANAGER) {
                if (employee != null) {
                    List<EmployeeDto> team = employeeService.getDirectReports(employee.getEmpId());
                    model.addAttribute("teamCount", team.size());
                    model.addAttribute("team", team); // Add team list for the table
                    
                    org.springframework.data.domain.Page<LeaveDto> pendingLeavesPage = leaveService.getPendingLeavesForManager(employee.getEmpId(), 0, 100, "leaveId");
                    model.addAttribute("pendingLeavesCount", pendingLeavesPage.getTotalElements());

                    // Dynamic Pending Reviews Count
                    org.springframework.data.domain.Page<PerformanceReviewDto> teamReviewsPage = performanceService.getTeamReviews(employee.getEmpId(), 0, 100, "reviewId");
                    long pendingReviewsCount = teamReviewsPage.getContent().stream()
                            .filter(r -> "SUBMITTED".equals(r.getStatus()))
                            .count();
                    model.addAttribute("pendingReviewsCount", pendingReviewsCount);

                    // Dynamic Team on Leave Count (Simplified: items in TeamLeaves are usually upcoming/current)
                    // In a real app, check if today is between startDate and endDate
                    org.springframework.data.domain.Page<LeaveDto> teamLeavesPage = leaveService.getTeamLeaves(employee.getEmpId(), 0, 100, "leaveId");
                    java.time.LocalDate today = java.time.LocalDate.now();
                    long onLeaveCount = teamLeavesPage.getContent().stream()
                            .filter(l -> "APPROVED".equals(l.getStatus()) && 
                                    !today.isBefore(l.getStartDate()) && !today.isAfter(l.getEndDate()))
                            .count();
                    model.addAttribute("onLeaveCount", onLeaveCount);
                }
                return "manager/dashboard";
            } else {
                if (employee != null) {
                    List<com.rev.app.entity.LeaveBalance> balances = leaveService.getLeaveBalances(employee.getEmpId());
                    double totalBalance = balances.stream().mapToDouble(b -> b.getBalanceDays()).sum();
                    model.addAttribute("totalLeaveBalance", totalBalance);
                    
                    org.springframework.data.domain.Page<PerformanceReviewDto> reviewsPage = performanceService.getEmployeeReviews(employee.getEmpId(), 0, 100, "reviewId");
                    List<PerformanceReviewDto> reviews = reviewsPage.getContent();
                    if (!reviews.isEmpty()) {
                        // Assuming reviews are ordered by year or ID, grab the latest one
                        PerformanceReviewDto latestReview = reviews.get(reviews.size() - 1);
                        model.addAttribute("latestReview", latestReview);
                    }
                }
            }
        }

        return "dashboard";
    }

    @GetMapping("/announcements")
    public String announcements(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
            Model model) {
        org.springframework.data.domain.Page<com.rev.app.dto.AnnouncementDto> announcementsPage = announcementService.getAllAnnouncements(page, size);
        model.addAttribute("page", announcementsPage);
        return "announcements";
    }
}
