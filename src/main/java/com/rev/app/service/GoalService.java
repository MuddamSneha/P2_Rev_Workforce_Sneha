package com.rev.app.service;

import com.rev.app.dto.GoalDto;

public interface GoalService {
    GoalDto createGoal(GoalDto goalDto);

    GoalDto updateGoalStatus(Long goalId, String status, Integer progress);

    org.springframework.data.domain.Page<GoalDto> getEmployeeGoals(String empId, int page, int size, String sortBy);
}
