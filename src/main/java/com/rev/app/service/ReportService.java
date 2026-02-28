package com.rev.app.service;

import com.rev.app.dto.DashboardMetricsDto;
import com.rev.app.dto.EmployeeReportDto;
import com.rev.app.dto.LeaveReportDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    DashboardMetricsDto getDashboardMetrics();
    List<EmployeeReportDto> getEmployeeReport(Long departmentId, String status);
    List<LeaveReportDto> getLeaveUtilizationReport(LocalDate startDate, LocalDate endDate, Long departmentId);
}
