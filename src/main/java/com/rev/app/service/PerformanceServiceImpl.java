package com.rev.app.service;

import com.rev.app.dto.PerformanceReviewDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.Goal;
import com.rev.app.entity.PerformanceReview;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.GoalRepository;
import com.rev.app.repository.PerformanceReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceServiceImpl implements PerformanceService {

        @Autowired
        private PerformanceReviewRepository performanceReviewRepository;

        @Autowired
        private EmployeeRepository employeeRepository;

        @Autowired
        private GoalRepository goalRepository;

        @Autowired
        private DTOMapper dtoMapper;

        @Autowired
        private NotificationService notificationService;

        @Override
        @Transactional
        public PerformanceReviewDto submitReview(PerformanceReviewDto dto) {
                Employee emp = employeeRepository.findById(dto.getEmpId())
                                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmpId()));

                PerformanceReview review = new PerformanceReview(
                                null,
                                emp,
                                dto.getReviewYear(),
                                dto.getAchievements(),
                                dto.getImprovements(),
                                dto.getSelfRating(),
                                null,
                                null,
                                "SUBMITTED"
                );

                PerformanceReview saved = performanceReviewRepository.save(review);

                if (emp.getManager() != null) {
                        notificationService.sendNotification(emp.getManager().getUser().getUserId(),
                                        "Performance review submitted by " + emp.getFirstName() + " "
                                                        + emp.getLastName(),
                                        "PERFORMANCE_SUBMITTED");
                }

                return dtoMapper.toPerformanceReviewDto(saved);
        }

        @Override
        @Transactional
        public PerformanceReviewDto provideFeedback(Long reviewId, BigDecimal rating, String feedback) {
                PerformanceReview review = performanceReviewRepository.findById(reviewId)
                                .orElseThrow(() -> new ResourceNotFoundException("Performance review not found with ID: " + reviewId));

                review.setManagerRating(rating);
                review.setManagerFeedback(feedback);
                review.setStatus("REVIEWED");

                PerformanceReview saved = performanceReviewRepository.save(review);

                notificationService.sendNotification(review.getEmployee().getUser().getUserId(),
                                "Your manager has provided feedback on your performance review",
                                "PERFORMANCE_REVIEWED");

                return dtoMapper.toPerformanceReviewDto(saved);
        }

        @Override
        public List<PerformanceReviewDto> getEmployeeReviews(String empId) {
                return performanceReviewRepository.findByEmployee_EmpId(empId).stream()
                                .map(dtoMapper::toPerformanceReviewDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<PerformanceReviewDto> getTeamReviews(String managerId) {
                return performanceReviewRepository.findByEmployee_Manager_EmpId(managerId).stream()
                                .map(dtoMapper::toPerformanceReviewDto)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void reviewGoal(Long goalId, String comment) {
                Goal goal = goalRepository.findById(goalId)
                                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with ID: " + goalId));
                goal.setManagerComment(comment);
                goalRepository.save(goal);
        }
}
