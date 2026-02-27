package com.rev.app.repository;

import com.rev.app.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByEmployee_EmpId(String empId);

    List<Goal> findByEmployee_Manager_EmpId(String managerId);
}
