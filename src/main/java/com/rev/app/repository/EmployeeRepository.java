package com.rev.app.repository;

import com.rev.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUser_UserId(Long userId);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.manager m LEFT JOIN FETCH m.user WHERE e.empId = :empId")
    Optional<Employee> findByIdWithManagerAndUser(@Param("empId") String empId);

    @Query("SELECT e FROM Employee e WHERE e.manager.empId = :managerId")
    List<Employee> findByManagerId(@Param("managerId") String managerId);

    Optional<Employee> findByUser_Email(String email);

    List<Employee> findByDepartment_DepartmentId(Long departmentId);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.empId) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.designation.designationName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.department.departmentName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchEmployees(@Param("query") String query);
}
