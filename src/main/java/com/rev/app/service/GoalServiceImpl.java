package com.rev.app.service;

import com.rev.app.dto.GoalDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.Goal;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalServiceImpl implements GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Override
    @Transactional
    public GoalDto createGoal(GoalDto dto) {
        Employee emp = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmpId()));

        Goal goal = new Goal();
        goal.setEmployee(emp);
        goal.setGoalDesc(dto.getGoalDesc());
        goal.setDeadline(dto.getDeadline());
        goal.setPriority(dto.getPriority());
        goal.setSuccessMetric(dto.getSuccessMetric());
        goal.setProgress(0);
        goal.setStatus("NOT_STARTED");

        return dtoMapper.toGoalDto(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public GoalDto updateGoalStatus(Long goalId, String status, Integer progress) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with ID: " + goalId));

        goal.setStatus(status);
        goal.setProgress(progress);

        return dtoMapper.toGoalDto(goalRepository.save(goal));
    }

    @Override
    public List<GoalDto> getEmployeeGoals(String empId) {
        return goalRepository.findByEmployee_EmpId(empId).stream()
                .map(dtoMapper::toGoalDto)
                .collect(Collectors.toList());
    }
}
