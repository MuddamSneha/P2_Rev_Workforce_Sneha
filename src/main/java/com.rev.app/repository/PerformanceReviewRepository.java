package com.rev.app.repository;

import com.rev.app.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployee_EmpId(String empId);

    List<PerformanceReview> findByEmployee_Manager_EmpId(String managerId);
}
