package com.rev.app.service;

import com.rev.app.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(String empId);

    EmployeeDto getEmployeeByUserId(Long userId);

    List<EmployeeDto> getAllEmployees();

    List<EmployeeDto> searchEmployees(String query);

    List<EmployeeDto> getDirectReports(String managerId);

    void deleteEmployee(String empId);
    
    void reactivateEmployee(String empId);
    
    EmployeeDto updateProfile(String empId, EmployeeDto profileDto);

    boolean isEmployeeExists(String empId);
}
