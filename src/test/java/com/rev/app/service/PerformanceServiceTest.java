package com.rev.app.service;

import com.rev.app.dto.PerformanceReviewDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.Goal;
import com.rev.app.entity.PerformanceReview;
import com.rev.app.entity.User;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.GoalRepository;
import com.rev.app.repository.PerformanceReviewRepository;
import com.rev.app.service.NotificationService;
import com.rev.app.service.PerformanceServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PerformanceServiceTest {

    private AutoCloseable closeable;

    @Mock
    private PerformanceReviewRepository performanceReviewRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private DTOMapper dtoMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PerformanceServiceImpl performanceService;

    private Employee employee;
    private Employee manager;
    private User user;
    private User managerUser;
    private PerformanceReview review;
    private PerformanceReviewDto reviewDto;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        managerUser = new User();
        managerUser.setUserId(2L);

        manager = new Employee();
        manager.setEmpId("MGR001");
        manager.setUser(managerUser);

        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setUser(user);
        employee.setManager(manager);

        review = new PerformanceReview();
        review.setReviewId(1L);
        review.setEmployee(employee);
        review.setReviewYear(2023);
        review.setStatus("SUBMITTED");

        reviewDto = new PerformanceReviewDto();
        reviewDto.setEmpId("EMP001");
        reviewDto.setReviewYear(2023);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenSubmitReview_thenReturnReviewDto() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(review);
        when(dtoMapper.toPerformanceReviewDto(any(PerformanceReview.class))).thenReturn(reviewDto);

        PerformanceReviewDto result = performanceService.submitReview(reviewDto);

        assertThat(result.getReviewYear()).isEqualTo(2023);
        verify(performanceReviewRepository).save(any(PerformanceReview.class));
        verify(notificationService).sendNotification(eq(2L), anyString(), eq("PERFORMANCE_SUBMITTED"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSubmitReviewEmployeeNotFound_thenThrowException() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        performanceService.submitReview(reviewDto);
    }

    @Test
    public void whenProvideFeedback_thenReturnReviewDto() {
        when(performanceReviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(review);
        when(dtoMapper.toPerformanceReviewDto(any(PerformanceReview.class))).thenReturn(reviewDto);

        PerformanceReviewDto result = performanceService.provideFeedback(1L, new BigDecimal("4.5"), "Good job");

        assertThat(review.getManagerRating()).isEqualTo(new BigDecimal("4.5"));
        assertThat(review.getStatus()).isEqualTo("REVIEWED");
        verify(performanceReviewRepository).save(any(PerformanceReview.class));
        verify(notificationService).sendNotification(eq(1L), anyString(), eq("PERFORMANCE_REVIEWED"));
    }

    @Test
    public void whenGetEmployeeReviews_thenReturnPageOfDtos() {
        Page<PerformanceReview> page = new PageImpl<>(Arrays.asList(review));
        when(performanceReviewRepository.findByEmployee_EmpId(anyString(), any(Pageable.class))).thenReturn(page);
        when(dtoMapper.toPerformanceReviewDto(any(PerformanceReview.class))).thenReturn(reviewDto);

        Page<PerformanceReviewDto> result = performanceService.getEmployeeReviews("EMP001", 0, 10, "id");

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    public void whenSubmitQuickRating_thenCreateNewReview() {
        Page<PerformanceReview> emptyPage = new PageImpl<>(java.util.Collections.emptyList());
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(performanceReviewRepository.findByEmployee_EmpId(anyString(), any(Pageable.class))).thenReturn(emptyPage);
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(review);
        when(dtoMapper.toPerformanceReviewDto(any(PerformanceReview.class))).thenReturn(reviewDto);

        PerformanceReviewDto result = performanceService.submitQuickRating("EMP001", new BigDecimal("4.0"), "Good");

        verify(performanceReviewRepository).save(any(PerformanceReview.class));
        verify(notificationService).sendNotification(eq(1L), anyString(), eq("PERFORMANCE_ALERT"));
    }

    @Test
    public void whenReviewGoal_thenSaveGoal() {
        Goal goal = new Goal();
        goal.setGoalId(1L);
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        performanceService.reviewGoal(1L, "Good progress");

        assertThat(goal.getManagerComment()).isEqualTo("Good progress");
        verify(goalRepository).save(any(Goal.class));
    }
}
