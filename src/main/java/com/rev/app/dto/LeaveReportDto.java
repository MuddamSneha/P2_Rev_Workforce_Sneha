package com.rev.app.dto;

import java.time.LocalDate;

public class LeaveReportDto {
    private String empId;
    private String employeeName;
    private String departmentName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationDays;
    private String status;

    public LeaveReportDto() {}

    public LeaveReportDto(String empId, String employeeName, String departmentName, String leaveTypeName, LocalDate startDate, LocalDate endDate, int durationDays, String status) {
        this.empId = empId;
        this.employeeName = employeeName;
        this.departmentName = departmentName;
        this.leaveTypeName = leaveTypeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = durationDays;
        this.status = status;
    }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public void setLeaveTypeName(String leaveTypeName) { this.leaveTypeName = leaveTypeName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
