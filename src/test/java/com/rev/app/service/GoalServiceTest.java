package com.rev.app.service;

import com.rev.app.dto.GoalDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.Goal;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.GoalRepository;
import com.rev.app.service.GoalServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GoalServiceTest {

    private AutoCloseable closeable;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    private Goal goal;
    private GoalDto goalDto;
    private Employee employee;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        employee = new Employee();
        employee.setEmpId("EMP001");

        goal = new Goal();
        goal.setGoalId(1L);
        goal.setGoalDesc("Test Goal");
        goal.setStatus("NOT_STARTED");
        goal.setEmployee(employee);

        goalDto = new GoalDto();
        goalDto.setEmpId("EMP001");
        goalDto.setGoalDesc("Test Goal");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenCreateGoal_thenReturnGoalDto() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(dtoMapper.toGoalDto(any(Goal.class))).thenReturn(goalDto);

        GoalDto created = goalService.createGoal(goalDto);

        assertThat(created.getGoalDesc()).isEqualTo("Test Goal");
        verify(goalRepository).save(any(Goal.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenCreateGoalEmployeeNotFound_thenThrowException() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        goalService.createGoal(goalDto);
    }

    @Test
    public void whenUpdateGoalStatus_thenReturnGoalDto() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(dtoMapper.toGoalDto(any(Goal.class))).thenReturn(goalDto);

        GoalDto updated = goalService.updateGoalStatus(1L, "IN_PROGRESS", 50);

        assertThat(updated.getGoalDesc()).isEqualTo("Test Goal");
        verify(goalRepository).save(any(Goal.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateGoalStatusNotFound_thenThrowException() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        goalService.updateGoalStatus(1L, "IN_PROGRESS", 50);
    }

    @Test
    public void whenGetEmployeeGoals_thenReturnPageOfDtos() {
        Page<Goal> page = new PageImpl<>(Arrays.asList(goal));
        when(goalRepository.findByEmployee_EmpId(anyString(), any(Pageable.class))).thenReturn(page);
        when(dtoMapper.toGoalDto(any(Goal.class))).thenReturn(goalDto);

        Page<GoalDto> result = goalService.getEmployeeGoals("EMP001", 0, 10, "goalId");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGoalDesc()).isEqualTo("Test Goal");
    }
}
