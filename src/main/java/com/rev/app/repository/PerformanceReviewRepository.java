package com.rev.app.repository;

import com.rev.app.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    org.springframework.data.domain.Page<PerformanceReview> findByEmployee_EmpId(String empId, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<PerformanceReview> findByEmployee_Manager_EmpId(String managerId, org.springframework.data.domain.Pageable pageable);
}
