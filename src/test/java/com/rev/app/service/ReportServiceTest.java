package com.rev.app.service;

import com.rev.app.dto.DashboardMetricsDto;
import com.rev.app.dto.EmployeeReportDto;
import com.rev.app.dto.LeaveReportDto;
import com.rev.app.entity.*;
import com.rev.app.repository.AnnouncementRepository;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveApplicationRepository;
import com.rev.app.service.ReportServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ReportServiceTest {

    private AutoCloseable closeable;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AnnouncementRepository announcementRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Employee employee;
    private User user;
    private Department department;
    private Designation designation;
    private LeaveApplication leave;
    private LeaveType leaveType;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        user = new User();
        user.setIsActive(1);
        user.setEmail("test@email.com");

        department = new Department(1L, "IT");
        designation = new Designation(1L, "Developer");

        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setUser(user);
        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setJoiningDate(LocalDate.now());

        leaveType = new LeaveType(1L, "Annual Leave", 20);

        leave = new LeaveApplication();
        leave.setLeaveId(1L);
        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
        leave.setStartDate(LocalDate.now());
        leave.setEndDate(LocalDate.now().plusDays(2));
        leave.setStatus("APPROVED");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenGetDashboardMetrics_thenReturnMetrics() {
        when(employeeRepository.count()).thenReturn(10L);
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));
        when(departmentRepository.count()).thenReturn(5L);
        when(leaveApplicationRepository.findByStatus("APPROVED")).thenReturn(Arrays.asList(leave));
        when(leaveApplicationRepository.findByStatus("PENDING")).thenReturn(Arrays.asList(leave));
        when(announcementRepository.count()).thenReturn(2L);

        DashboardMetricsDto metrics = reportService.getDashboardMetrics();

        assertThat(metrics.getTotalEmployees()).isEqualTo(10L);
        assertThat(metrics.getActiveEmployees()).isEqualTo(1L);
        assertThat(metrics.getTotalDepartments()).isEqualTo(5L);
        assertThat(metrics.getActiveLeavesToday()).isEqualTo(1L);
        assertThat(metrics.getPendingLeaveRequests()).isEqualTo(1L);
        assertThat(metrics.getOpenAnnouncements()).isEqualTo(2L);
    }

    @Test
    public void whenGetEmployeeReportWithDept_thenReturnList() {
        when(employeeRepository.findByDepartment_DepartmentId(anyLong())).thenReturn(Arrays.asList(employee));

        List<EmployeeReportDto> report = reportService.getEmployeeReport(1L, "Active");

        assertThat(report).hasSize(1);
        assertThat(report.get(0).getEmpId()).isEqualTo("EMP001");
        assertThat(report.get(0).getDepartmentName()).isEqualTo("IT");
    }

    @Test
    public void whenGetEmployeeReportWithoutDept_thenReturnList() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));

        List<EmployeeReportDto> report = reportService.getEmployeeReport(null, "Active");

        assertThat(report).hasSize(1);
        assertThat(report.get(0).getEmpId()).isEqualTo("EMP001");
    }

    @Test
    public void whenGetLeaveUtilizationReport_thenReturnFilteredList() {
        when(leaveApplicationRepository.findAll()).thenReturn(Arrays.asList(leave));

        List<LeaveReportDto> report = reportService.getLeaveUtilizationReport(LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), null);

        assertThat(report).hasSize(1);
        assertThat(report.get(0).getEmpId()).isEqualTo("EMP001");
        assertThat(report.get(0).getLeaveTypeName()).isEqualTo("Annual Leave");
    }
}
