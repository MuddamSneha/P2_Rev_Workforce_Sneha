package com.rev.app.service;

import com.rev.app.dto.LeaveDto;
import java.util.List;

public interface LeaveService {
    LeaveDto applyLeave(LeaveDto leaveDto);

    LeaveDto approveLeave(Long leaveId, String managerId, String comment);

    LeaveDto rejectLeave(Long leaveId, String managerId, String comment);

    LeaveDto cancelLeave(Long leaveId);

    List<LeaveDto> getEmployeeLeaves(String empId);

    List<LeaveDto> getPendingLeavesForManager(String managerId);

    List<LeaveDto> getAllLeaves();

    List<com.rev.app.entity.LeaveBalance> getLeaveBalances(String empId);

    List<LeaveDto> getTeamLeaves(String managerId);
    List<com.rev.app.entity.LeaveType> getAllLeaveTypes();

    com.rev.app.entity.LeaveType saveLeaveType(com.rev.app.entity.LeaveType leaveType);

    void deleteLeaveType(Long id);

    void adjustBalance(String empId, Long leaveTypeId, Integer adjustment);

    List<com.rev.app.entity.LeaveBalance> getAllBalances();

    List<LeaveDto> searchLeaves(String empId, Long deptId, String status);
}
