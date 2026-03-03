package com.rev.app.service;

import com.rev.app.dto.LeaveDto;
import com.rev.app.entity.*;
import com.rev.app.exceptions.BusinessException;
import com.rev.app.exceptions.InsufficientBalanceException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveApplicationRepository;
import com.rev.app.repository.LeaveBalanceRepository;
import com.rev.app.repository.LeaveTypeRepository;
import com.rev.app.service.LeaveServiceImpl;
import com.rev.app.service.NotificationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyLong;

public class LeaveServiceTest {

    private AutoCloseable closeable;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DTOMapper dtoMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private Employee employee;
    private Employee manager;
    private User user;
    private User managerUser;
    private LeaveType leaveType;
    private LeaveApplication leaveApplication;
    private LeaveBalance leaveBalance;
    private LeaveDto leaveDto;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        managerUser = new User();
        managerUser.setUserId(2L);

        manager = new Employee();
        manager.setEmpId("MGR001");
        manager.setUser(managerUser);

        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setUser(user);
        employee.setManager(manager);

        leaveType = new LeaveType(1L, "Annual Leave", 20);

        leaveApplication = new LeaveApplication(1L, employee, leaveType, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Vacation", "PENDING", null, null, null);

        leaveBalance = new LeaveBalance(1L, employee, leaveType, 10);

        leaveDto = new LeaveDto();
        leaveDto.setEmpId("EMP001");
        leaveDto.setLeaveTypeId(1L);
        leaveDto.setStartDate(LocalDate.now().plusDays(1));
        leaveDto.setEndDate(LocalDate.now().plusDays(2));
        leaveDto.setReason("Vacation");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenApplyLeave_thenReturnLeaveDto() {
        when(employeeRepository.findByIdWithManagerAndUser(anyString())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(anyLong())).thenReturn(Optional.of(leaveType));
        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(leaveApplication);
        when(dtoMapper.toLeaveDto(any(LeaveApplication.class))).thenReturn(leaveDto);
        doNothing().when(notificationService).sendNotification(anyLong(), anyString(), anyString());

        LeaveDto result = leaveService.applyLeave(leaveDto);

        assertThat(result.getReason()).isEqualTo("Vacation");
        verify(leaveApplicationRepository).save(any(LeaveApplication.class));
        verify(notificationService).sendNotification(eq(2L), anyString(), eq("LEAVE_REQUEST"));
    }

    @Test(expected = BusinessException.class)
    public void whenApplyLeavePastDate_thenThrowException() {
        leaveDto.setStartDate(LocalDate.now().minusDays(1));
        leaveService.applyLeave(leaveDto);
    }

    @Test(expected = BusinessException.class)
    public void whenApplyLeaveEndBeforeStart_thenThrowException() {
        leaveDto.setEndDate(LocalDate.now());
        leaveDto.setStartDate(LocalDate.now().plusDays(1));
        leaveService.applyLeave(leaveDto);
    }

    @Test
    public void whenApproveLeave_thenReturnLeaveDto() {
        when(leaveApplicationRepository.findById(anyLong())).thenReturn(Optional.of(leaveApplication));
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(manager));
        when(leaveBalanceRepository.findByEmployee_EmpIdAndLeaveType_LeaveTypeId(anyString(), anyLong())).thenReturn(Optional.of(leaveBalance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(leaveBalance);
        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(leaveApplication);
        when(dtoMapper.toLeaveDto(any(LeaveApplication.class))).thenReturn(leaveDto);
        doNothing().when(notificationService).sendNotification(anyLong(), anyString(), anyString());

        LeaveDto result = leaveService.approveLeave(1L, "MGR001", "Approved");

        assertThat(leaveApplication.getStatus()).isEqualTo("APPROVED");
        assertThat(leaveBalance.getBalanceDays()).isEqualTo(8); // 10 original - 2 days leave
        verify(leaveBalanceRepository).save(any(LeaveBalance.class));
        verify(leaveApplicationRepository).save(any(LeaveApplication.class));
    }

    @Test(expected = InsufficientBalanceException.class)
    public void whenApproveLeaveInsufficientBalance_thenThrowException() {
        leaveBalance.setBalanceDays(1); // Not enough for 2 days leave
        when(leaveApplicationRepository.findById(anyLong())).thenReturn(Optional.of(leaveApplication));
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(manager));
        when(leaveBalanceRepository.findByEmployee_EmpIdAndLeaveType_LeaveTypeId(anyString(), anyLong())).thenReturn(Optional.of(leaveBalance));

        leaveService.approveLeave(1L, "MGR001", "Approved");
    }

    @Test
    public void whenRejectLeave_thenReturnLeaveDto() {
        when(leaveApplicationRepository.findById(anyLong())).thenReturn(Optional.of(leaveApplication));
        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(leaveApplication);
        when(dtoMapper.toLeaveDto(any(LeaveApplication.class))).thenReturn(leaveDto);

        LeaveDto result = leaveService.rejectLeave(1L, "MGR001", "Rejected");

        assertThat(leaveApplication.getStatus()).isEqualTo("REJECTED");
        verify(leaveApplicationRepository).save(any(LeaveApplication.class));
    }

    @Test
    public void whenCancelLeave_thenReturnLeaveDto() {
        when(leaveApplicationRepository.findById(anyLong())).thenReturn(Optional.of(leaveApplication));
        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(leaveApplication);
        when(dtoMapper.toLeaveDto(any(LeaveApplication.class))).thenReturn(leaveDto);

        LeaveDto result = leaveService.cancelLeave(1L);

        assertThat(leaveApplication.getStatus()).isEqualTo("CANCELLED");
        verify(leaveApplicationRepository).save(any(LeaveApplication.class));
    }

    @Test(expected = BusinessException.class)
    public void whenCancelNonPendingLeave_thenThrowException() {
        leaveApplication.setStatus("APPROVED");
        when(leaveApplicationRepository.findById(anyLong())).thenReturn(Optional.of(leaveApplication));

        leaveService.cancelLeave(1L);
    }
}
