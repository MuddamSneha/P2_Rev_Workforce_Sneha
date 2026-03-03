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
                                dto.getKeyDeliverables(),
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
        public org.springframework.data.domain.Page<PerformanceReviewDto> getEmployeeReviews(String empId, int page, int size, String sortBy) {
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
                return performanceReviewRepository.findByEmployee_EmpId(empId, pageable)
                                .map(dtoMapper::toPerformanceReviewDto);
        }

        @Override
        public org.springframework.data.domain.Page<PerformanceReviewDto> getTeamReviews(String managerId, int page, int size, String sortBy) {
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
                return performanceReviewRepository.findByEmployee_Manager_EmpId(managerId, pageable)
                                .map(dtoMapper::toPerformanceReviewDto);
        }

        @Override
        @Transactional
        public PerformanceReviewDto submitQuickRating(String empId, BigDecimal rating, String comment) {
                Employee emp = employeeRepository.findById(empId)
                                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));
                
                int currentYear = java.time.Year.now().getValue();
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 100, org.springframework.data.domain.Sort.by("reviewYear").descending());
                org.springframework.data.domain.Page<PerformanceReview> existingReviewsPage = performanceReviewRepository.findByEmployee_EmpId(empId, pageable);
                PerformanceReview review = existingReviewsPage.getContent().stream()
                                .filter(r -> r.getReviewYear() != null && r.getReviewYear() == currentYear)
                                .findFirst()
                                .orElse(null);
                
                if (review == null) {
                        review = new PerformanceReview(
                                null,
                                emp,
                                currentYear,
                                "Quick rating submitted by manager", // achievements
                                "Quick rating submitted by manager", // improvements
                                "Quick rating submitted by manager", // keyDeliverables
                                null, // selfRating
                                rating, // managerRating
                                comment, // managerFeedback
                                "REVIEWED"
                        );
                } else {
                        review.setManagerRating(rating);
                        review.setManagerFeedback(comment);
                        review.setStatus("REVIEWED");
                }
                
                PerformanceReview saved = performanceReviewRepository.save(review);
                if (emp.getUser() != null) {
                    notificationService.sendNotification(emp.getUser().getUserId(),
                                "Your manager added a performance rating: " + rating + "/5",
                                "PERFORMANCE_ALERT");
                }
                return dtoMapper.toPerformanceReviewDto(saved);
        }

        @Override
        public PerformanceReviewDto getReviewById(Long reviewId) {
                PerformanceReview review = performanceReviewRepository.findById(reviewId)
                                .orElseThrow(() -> new ResourceNotFoundException("Performance review not found with ID: " + reviewId));
                return dtoMapper.toPerformanceReviewDto(review);
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
