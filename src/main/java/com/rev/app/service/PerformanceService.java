package com.rev.app.service;

import com.rev.app.dto.PerformanceReviewDto;

import java.math.BigDecimal;
import java.util.List;

public interface PerformanceService {
    PerformanceReviewDto submitReview(PerformanceReviewDto reviewDto);

    PerformanceReviewDto provideFeedback(Long reviewId, BigDecimal rating, String feedback);

    List<PerformanceReviewDto> getEmployeeReviews(String empId);

    List<PerformanceReviewDto> getTeamReviews(String managerId);

    PerformanceReviewDto getReviewById(Long reviewId);

    void reviewGoal(Long goalId, String comment);
}
