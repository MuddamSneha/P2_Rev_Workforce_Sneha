package com.rev.app.repository;

import com.rev.app.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    org.springframework.data.domain.Page<LeaveApplication> findByEmployee_EmpId(String empId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.empId = :managerId AND la.status = 'PENDING'")
    org.springframework.data.domain.Page<LeaveApplication> findPendingByManagerId(@Param("managerId") String managerId, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<LeaveApplication> findByEmployee_Manager_EmpId(String managerId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la WHERE (:empId IS NULL OR LOWER(la.employee.empId) LIKE LOWER(CONCAT('%', :empId, '%'))) " +
           "AND (:deptId IS NULL OR la.employee.department.departmentId = :deptId) " +
           "AND (:status IS NULL OR :status = 'ALL' OR la.status = :status)")
    org.springframework.data.domain.Page<LeaveApplication> searchLeaves(@Param("empId") String empId, @Param("deptId") Long deptId, @Param("status") String status, org.springframework.data.domain.Pageable pageable);

    List<LeaveApplication> findByStatus(String status);
}
