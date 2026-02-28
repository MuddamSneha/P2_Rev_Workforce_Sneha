package com.rev.app.service;

import com.rev.app.dto.LeaveDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.LeaveApplication;
import com.rev.app.entity.LeaveBalance;
import com.rev.app.entity.LeaveType;
import com.rev.app.exceptions.BusinessException;
import com.rev.app.exceptions.InsufficientBalanceException;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.LeaveApplicationRepository;
import com.rev.app.repository.LeaveBalanceRepository;
import com.rev.app.repository.LeaveTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    private static final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public LeaveDto applyLeave(LeaveDto dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new BusinessException("Start date and end date must be provided");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }
        if (dto.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("Cannot apply for a leave in the past");
        }

        Employee emp = employeeRepository.findByIdWithManagerAndUser(dto.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmpId()));
        LeaveType type = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with ID: " + dto.getLeaveTypeId()));

        LeaveApplication leave = new LeaveApplication(
                null,
                emp,
                type,
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getReason(),
                "PENDING",
                null,
                null,
                null
        );

        LeaveApplication saved = leaveApplicationRepository.save(leave);

        log.debug("Leave saved with ID: {}", saved.getLeaveId());
        log.debug("Employee: {} ({}), Manager: {}", emp.getFirstName(), emp.getEmpId(),
                emp.getManager() != null ? emp.getManager().getEmpId() : "NULL");

        if (emp.getManager() != null) {
            try {
                Long managerUserId = emp.getManager().getUser().getUserId();
                log.debug("Sending notification to manager userId: {}", managerUserId);
                notificationService.sendNotification(managerUserId,
                        "New leave request from " + emp.getFirstName() + " " + emp.getLastName(), "LEAVE_REQUEST");
                log.debug("Notification sent successfully");
            } catch (Exception e) {
                log.error("Failed to send notification to manager: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No manager found for employee {} — no notification sent", emp.getEmpId());
        }

        return dtoMapper.toLeaveDto(saved);
    }

    @Override
    @Transactional
    public LeaveDto approveLeave(Long leaveId, String managerId, String comment) {
        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found with ID: " + leaveId));
        Employee manager = employeeRepository.findById(managerId).orElse(null);

        long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;

        LeaveBalance balance = leaveBalanceRepository.findByEmployee_EmpIdAndLeaveType_LeaveTypeId(
                leave.getEmployee().getEmpId(), leave.getLeaveType().getLeaveTypeId())
                .orElseGet(() -> {
                    log.info("Creating missing leave balance for employee {} and leave type {}", 
                            leave.getEmployee().getEmpId(), leave.getLeaveType().getLeaveName());
                    LeaveBalance newBalance = new LeaveBalance(null, leave.getEmployee(), leave.getLeaveType(), 0);
                    return leaveBalanceRepository.save(newBalance);
                });

        if (balance.getBalanceDays() < days) {
            throw new InsufficientBalanceException("Insufficient leave balance. Required: " + days + ", Available: " + balance.getBalanceDays());
        }

        balance.setBalanceDays(balance.getBalanceDays() - (int) days);
        leaveBalanceRepository.save(balance);

        leave.setStatus("APPROVED");
        leave.setApprovedBy(manager);
        leave.setManagerComment(comment);

        LeaveDto result = dtoMapper.toLeaveDto(leaveApplicationRepository.save(leave));

        notificationService.sendNotification(leave.getEmployee().getUser().getUserId(),
                "Your leave request has been approved", "LEAVE_APPROVED");

        return result;
    }

    @Override
    @Transactional
    public LeaveDto rejectLeave(Long leaveId, String managerId, String comment) {
        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found with ID: " + leaveId));

        leave.setStatus("REJECTED");
        leave.setManagerComment(comment);

        LeaveDto result = dtoMapper.toLeaveDto(leaveApplicationRepository.save(leave));

        notificationService.sendNotification(leave.getEmployee().getUser().getUserId(),
                "Your leave request has been rejected", "LEAVE_REJECTED");

        return result;
    }

    @Override
    @Transactional
    public LeaveDto cancelLeave(Long leaveId) {
        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found with ID: " + leaveId));

        if (!"PENDING".equals(leave.getStatus())) {
            throw new BusinessException("Only pending leaves can be cancelled");
        }

        leave.setStatus("CANCELLED");
        return dtoMapper.toLeaveDto(leaveApplicationRepository.save(leave));
    }

    @Override
    public List<LeaveDto> getEmployeeLeaves(String empId) {
        return leaveApplicationRepository.findByEmployee_EmpId(empId).stream()
                .map(dtoMapper::toLeaveDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveDto> getPendingLeavesForManager(String managerId) {
        return leaveApplicationRepository.findPendingByManagerId(managerId).stream()
                .map(dtoMapper::toLeaveDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveDto> getAllLeaves() {
        return leaveApplicationRepository.findAll().stream()
                .map(dtoMapper::toLeaveDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<LeaveBalance> getLeaveBalances(String empId) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployee_EmpId(empId);
        if (balances.isEmpty()) {
            initializeLeaveBalances(empId);
            return leaveBalanceRepository.findByEmployee_EmpId(empId);
        }
        return balances;
    }

    @Override
    public List<LeaveDto> getTeamLeaves(String managerId) {
        return leaveApplicationRepository.findAll().stream()
                .filter(la -> la.getEmployee().getManager() != null && la.getEmployee().getManager().getEmpId().equals(managerId))
                .map(dtoMapper::toLeaveDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @Override
    public LeaveType saveLeaveType(LeaveType leaveType) {
        return leaveTypeRepository.save(leaveType);
    }

    @Override
    public void deleteLeaveType(Long id) {
        leaveTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void adjustBalance(String empId, Long leaveTypeId, Integer adjustment) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployee_EmpIdAndLeaveType_LeaveTypeId(empId, leaveTypeId)
                .orElseGet(() -> {
                    Employee emp = employeeRepository.findById(empId).orElseThrow();
                    LeaveType type = leaveTypeRepository.findById(leaveTypeId).orElseThrow();
                    return new LeaveBalance(
                            null,
                            emp,
                            type,
                            0
                    );
                });
        balance.setBalanceDays(balance.getBalanceDays() + adjustment);
        leaveBalanceRepository.save(balance);
    }

    @Override
    public List<LeaveBalance> getAllBalances() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public List<LeaveDto> searchLeaves(String empId, Long deptId, String status) {
        return leaveApplicationRepository.findAll().stream()
                .filter(la -> (empId == null || la.getEmployee().getEmpId().contains(empId)))
                .filter(la -> (deptId == null || (la.getEmployee().getDepartment() != null && la.getEmployee().getDepartment().getDepartmentId().equals(deptId))))
                .filter(la -> (status == null || "ALL".equals(status) || status.equals(la.getStatus())))
                .map(dtoMapper::toLeaveDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void initializeLeaveBalances(String empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));
        
        List<LeaveType> types = leaveTypeRepository.findAll();
        for (LeaveType type : types) {
            Optional<LeaveBalance> existing = leaveBalanceRepository.findByEmployee_EmpIdAndLeaveType_LeaveTypeId(empId, type.getLeaveTypeId());
            if (existing.isEmpty()) {
                LeaveBalance balance = new LeaveBalance(
                        null,
                        emp,
                        type,
                        type.getMaxPerYear() != null ? type.getMaxPerYear() : 0
                );
                leaveBalanceRepository.save(balance);
                log.info("Initialized {} balance for employee {}: {} days", type.getLeaveName(), empId, balance.getBalanceDays());
            }
        }
    }
}
