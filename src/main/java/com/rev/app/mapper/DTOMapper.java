package com.rev.app.mapper;

import com.rev.app.dto.*;
import com.rev.app.entity.*;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    public EmployeeDto toEmployeeDto(Employee employee) {
        if (employee == null)
            return null;
        EmployeeDto dto = new EmployeeDto();
        dto.setEmpId(employee.getEmpId());
        dto.setUserId(employee.getUser() != null ? employee.getUser().getUserId() : null);
        dto.setEmail(employee.getUser() != null ? employee.getUser().getEmail() : null);
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setPhone(employee.getPhone());
        dto.setAddress(employee.getAddress());
        dto.setEmergencyContact(employee.getEmergencyContact());
        dto.setDob(employee.getDob());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null);
        dto.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null);
        dto.setDesignationId(employee.getDesignation() != null ? employee.getDesignation().getDesignationId() : null);
        dto.setDesignation(employee.getDesignation() != null ? employee.getDesignation().getDesignationName() : null);
        dto.setManagerId(employee.getManager() != null ? employee.getManager().getEmpId() : null);
        dto.setManagerName(employee.getManager() != null
                ? employee.getManager().getFirstName() + " " + employee.getManager().getLastName()
                : null);
        dto.setSalary(employee.getSalary());
        dto.setSsId(employee.getSsId());
        dto.setRole(employee.getUser() != null ? employee.getUser().getRole().name() : null);
        dto.setIsActive(employee.getUser() != null ? employee.getUser().getIsActive() : null);
        return dto;
    }

    public LeaveDto toLeaveDto(LeaveApplication leave) {
        if (leave == null)
            return null;
        LeaveDto dto = new LeaveDto();
        dto.setLeaveId(leave.getLeaveId());
        dto.setEmpId(leave.getEmployee().getEmpId());
        dto.setEmployeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName());
        dto.setLeaveTypeId(leave.getLeaveType().getLeaveTypeId());
        dto.setLeaveTypeName(leave.getLeaveType().getLeaveName());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus());
        dto.setAppliedOn(leave.getAppliedOn());
        dto.setApprovedBy(leave.getApprovedBy() != null ? leave.getApprovedBy().getEmpId() : null);
        dto.setManagerComment(leave.getManagerComment());
        return dto;
    }

    public PerformanceReviewDto toPerformanceReviewDto(PerformanceReview review) {
        if (review == null)
            return null;
        PerformanceReviewDto dto = new PerformanceReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setEmpId(review.getEmployee().getEmpId());
        dto.setEmployeeName(review.getEmployee().getFirstName() + " " + review.getEmployee().getLastName());
        dto.setReviewYear(review.getReviewYear());
        dto.setAchievements(review.getAchievements());
        dto.setImprovements(review.getImprovements());
        dto.setSelfRating(review.getSelfRating());
        dto.setManagerRating(review.getManagerRating());
        dto.setManagerFeedback(review.getManagerFeedback());
        dto.setStatus(review.getStatus());
        return dto;
    }

    public GoalDto toGoalDto(Goal goal) {
        if (goal == null)
            return null;
        GoalDto dto = new GoalDto();
        dto.setGoalId(goal.getGoalId());
        dto.setEmpId(goal.getEmployee().getEmpId());
        dto.setGoalDesc(goal.getGoalDesc());
        dto.setDeadline(goal.getDeadline());
        dto.setPriority(goal.getPriority());
        dto.setSuccessMetric(goal.getSuccessMetric());
        dto.setProgress(goal.getProgress());
        dto.setStatus(goal.getStatus());
        return dto;
    }

    public AnnouncementDto toAnnouncementDto(Announcement announcement) {
        if (announcement == null)
            return null;
        AnnouncementDto dto = new AnnouncementDto();
        dto.setAnnouncementId(announcement.getAnnouncementId());
        dto.setTitle(announcement.getTitle());
        dto.setMessage(announcement.getMessage());
        dto.setCreatedBy(announcement.getCreatedBy().getEmpId());
        dto.setCreatorName(
                announcement.getCreatedBy().getFirstName() + " " + announcement.getCreatedBy().getLastName());
        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }

    public NotificationDto toNotificationDto(Notification notification) {
        if (notification == null)
            return null;
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getUserId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
