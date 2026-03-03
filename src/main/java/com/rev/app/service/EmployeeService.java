package com.rev.app.service;

import com.rev.app.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(String empId);

    EmployeeDto getEmployeeByUserId(Long userId);

    org.springframework.data.domain.Page<EmployeeDto> getAllEmployees(int page, int size, String sortBy);

    org.springframework.data.domain.Page<EmployeeDto> searchEmployees(String query, int page, int size, String sortBy);

    List<EmployeeDto> getDirectReports(String managerId);

    void deleteEmployee(String empId);
    
    void reactivateEmployee(String empId);
    
    EmployeeDto updateProfile(String empId, EmployeeDto profileDto);

    boolean isEmployeeExists(String empId);

    List<com.rev.app.repository.projection.EmployeeSummary> getEmployeeSummaries();

    List<EmployeeDto> filterEmployees(String department, String designation, String search);
}
