package com.rev.app.service;

import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.SystemLog;
import java.util.List;

public interface ConfigService {
    // Departments
    List<Department> getAllDepartments();
    Department saveDepartment(Department department);
    void deleteDepartment(Long id);

    // Designations
    List<Designation> getAllDesignations();
    Designation saveDesignation(Designation designation);
    void deleteDesignation(Long id);

    // Logs
    void logActivity(String action, String empId, String details);
    List<SystemLog> getAllLogs();
}
