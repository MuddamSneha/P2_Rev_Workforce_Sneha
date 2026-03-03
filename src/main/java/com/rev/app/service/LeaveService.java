package com.rev.app.service;

import com.rev.app.dto.LeaveDto;

import java.util.List;

public interface LeaveService {
    LeaveDto applyLeave(LeaveDto leaveDto);

    LeaveDto approveLeave(Long leaveId, String managerId, String comment);

    LeaveDto rejectLeave(Long leaveId, String managerId, String comment);

    LeaveDto cancelLeave(Long leaveId);

    org.springframework.data.domain.Page<LeaveDto> getEmployeeLeaves(String empId, int page, int size, String sortBy);

    org.springframework.data.domain.Page<LeaveDto> getPendingLeavesForManager(String managerId, int page, int size, String sortBy);

    org.springframework.data.domain.Page<LeaveDto> getAllLeaves(int page, int size, String sortBy);

    List<com.rev.app.entity.LeaveBalance> getLeaveBalances(String empId);

    org.springframework.data.domain.Page<LeaveDto> getTeamLeaves(String managerId, int page, int size, String sortBy);
    List<com.rev.app.entity.LeaveType> getAllLeaveTypes();

    com.rev.app.entity.LeaveType saveLeaveType(com.rev.app.entity.LeaveType leaveType);

    void deleteLeaveType(Long id);

    void adjustBalance(String empId, Long leaveTypeId, Integer adjustment);

    List<com.rev.app.entity.LeaveBalance> getAllBalances();

    org.springframework.data.domain.Page<LeaveDto> searchLeaves(String empId, Long deptId, String status, int page, int size, String sortBy);
    
    void initializeLeaveBalances(String empId);
}
