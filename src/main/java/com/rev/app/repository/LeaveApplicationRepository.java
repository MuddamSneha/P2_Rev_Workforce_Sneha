package com.rev.app.repository;

import com.rev.app.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByEmployee_EmpId(String empId);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.empId = :managerId AND la.status = 'PENDING'")
    List<LeaveApplication> findPendingByManagerId(@Param("managerId") String managerId);

    List<LeaveApplication> findByStatus(String status);
}
