package com.rev.app.repository;

import com.rev.app.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByEmployee_EmpId(String empId);

    Optional<LeaveBalance> findByEmployee_EmpIdAndLeaveType_LeaveTypeId(String empId, Long leaveTypeId);
}
