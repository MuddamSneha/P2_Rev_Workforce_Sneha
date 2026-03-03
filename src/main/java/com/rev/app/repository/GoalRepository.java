package com.rev.app.repository;

import com.rev.app.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    org.springframework.data.domain.Page<Goal> findByEmployee_EmpId(String empId, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<Goal> findByEmployee_Manager_EmpId(String managerId, org.springframework.data.domain.Pageable pageable);
}
