package com.rev.app.service;

import com.rev.app.dto.DashboardMetricsDto;
import com.rev.app.dto.EmployeeReportDto;
import com.rev.app.dto.LeaveReportDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.LeaveApplication;
import com.rev.app.repository.AnnouncementRepository;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public DashboardMetricsDto getDashboardMetrics() {
        long totalEmployees = employeeRepository.count();
        // Since we don't have a specific 'status' column yet in Employee, we'll assume all are active
        // for the purpose of the 'Active Users' metric, or count users with isActive = 1 if possible.
        long activeEmployees = employeeRepository.findAll().stream()
                .filter(e -> e.getUser() != null && e.getUser().getIsActive() == 1)
                .count();
        
        long totalDepartments = departmentRepository.count();
        long activeLeavesToday = leaveApplicationRepository.findByStatus("APPROVED").size();
        long pendingLeavesCount = leaveApplicationRepository.findByStatus("PENDING").size();
        long openAnnouncements = announcementRepository.count();

        return new DashboardMetricsDto(
                totalEmployees,
                activeEmployees,
                totalDepartments,
                activeLeavesToday,
                pendingLeavesCount,
                openAnnouncements
        );
    }

    @Override
    public List<EmployeeReportDto> getEmployeeReport(Long departmentId, String status) {
        List<Employee> employees;
        if (departmentId != null) {
            employees = employeeRepository.findByDepartment_DepartmentId(departmentId);
        } else {
            employees = employeeRepository.findAll();
        }

        // Apply status filter if provided (assuming Employee entity has a status or active flag, filtering in memory for brevity)
        // If status isn't part of Employee yet, we skip it or assume 'Active'
        
        return employees.stream().map(e -> new EmployeeReportDto(
                e.getEmpId(),
                e.getFirstName(),
                e.getLastName(),
                e.getUser().getEmail(),
                e.getDepartment() != null ? e.getDepartment().getDepartmentName() : "N/A",
                e.getDesignation() != null ? e.getDesignation().getDesignationName() : "N/A",
                e.getManager() != null ? e.getManager().getFirstName() + " " + e.getManager().getLastName() : "None",
                e.getJoiningDate(),
                "Active" // Placeholder, enhance if Employee has standard status field
        )).collect(Collectors.toList());
    }

    @Override
    public List<LeaveReportDto> getLeaveUtilizationReport(LocalDate startDate, LocalDate endDate, Long departmentId) {
        // Fetching all leaves for simplicity and filtering in memory. 
        // In a real app, use @Query to filter at the DB level for performance.
        List<LeaveApplication> allLeaves = leaveApplicationRepository.findAll();
        
        return allLeaves.stream()
                .filter(l -> (departmentId == null || (l.getEmployee().getDepartment() != null && l.getEmployee().getDepartment().getDepartmentId().equals(departmentId))))
                .filter(l -> (startDate == null || !l.getStartDate().isBefore(startDate)))
                .filter(l -> (endDate == null || !l.getEndDate().isAfter(endDate)))
                .map(l -> new LeaveReportDto(
                        l.getEmployee().getEmpId(),
                        l.getEmployee().getFirstName() + " " + l.getEmployee().getLastName(),
                        l.getEmployee().getDepartment() != null ? l.getEmployee().getDepartment().getDepartmentName() : "N/A",
                        l.getLeaveType() != null ? l.getLeaveType().getLeaveName() : "N/A",
                        l.getStartDate(),
                        l.getEndDate(),
                        (int) java.time.temporal.ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1,
                        l.getStatus()
                ))
                .collect(Collectors.toList());
    }
}
