package com.rev.app.dto;

public class DashboardMetricsDto {
    private long totalEmployees;
    private long activeEmployees;
    private long totalDepartments;
    private long activeLeavesToday;
    private long pendingLeaveRequests;
    private long openAnnouncements;

    public DashboardMetricsDto() {}

    public DashboardMetricsDto(long totalEmployees, long activeEmployees, long totalDepartments, 
                               long activeLeavesToday, long pendingLeaveRequests, long openAnnouncements) {
        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.totalDepartments = totalDepartments;
        this.activeLeavesToday = activeLeavesToday;
        this.pendingLeaveRequests = pendingLeaveRequests;
        this.openAnnouncements = openAnnouncements;
    }

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }
    
    public long getActiveEmployees() { return activeEmployees; }
    public void setActiveEmployees(long activeEmployees) { this.activeEmployees = activeEmployees; }
    
    public long getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(long totalDepartments) { this.totalDepartments = totalDepartments; }
    
    public long getActiveLeavesToday() { return activeLeavesToday; }
    public void setActiveLeavesToday(long activeLeavesToday) { this.activeLeavesToday = activeLeavesToday; }
    
    public long getPendingLeaveRequests() { return pendingLeaveRequests; }
    public void setPendingLeaveRequests(long pendingLeaveRequests) { this.pendingLeaveRequests = pendingLeaveRequests; }
    
    public long getOpenAnnouncements() { return openAnnouncements; }
    public void setOpenAnnouncements(long openAnnouncements) { this.openAnnouncements = openAnnouncements; }
}
