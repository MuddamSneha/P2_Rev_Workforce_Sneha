package com.rev.app.service;

import com.rev.app.dto.GoalDto;

import java.util.List;

public interface GoalService {
    GoalDto createGoal(GoalDto goalDto);

    GoalDto updateGoalStatus(Long goalId, String status, Integer progress);

    List<GoalDto> getEmployeeGoals(String empId);
}
