package com.rev.app.service;

import com.rev.app.dto.PerformanceReviewDto;

import java.math.BigDecimal;

public interface PerformanceService {
    PerformanceReviewDto submitReview(PerformanceReviewDto reviewDto);

    PerformanceReviewDto provideFeedback(Long reviewId, BigDecimal rating, String feedback);

    org.springframework.data.domain.Page<PerformanceReviewDto> getEmployeeReviews(String empId, int page, int size, String sortBy);

    org.springframework.data.domain.Page<PerformanceReviewDto> getTeamReviews(String managerId, int page, int size, String sortBy);

    PerformanceReviewDto getReviewById(Long reviewId);

    void reviewGoal(Long goalId, String comment);

    PerformanceReviewDto submitQuickRating(String empId, BigDecimal rating, String comment);
}
