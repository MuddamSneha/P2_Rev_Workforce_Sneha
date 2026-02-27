package com.rev.app.service;

import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.entity.SystemLog;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }

    @Override
    @Transactional
    public Designation saveDesignation(Designation designation) {
        return designationRepository.save(designation);
    }

    @Override
    @Transactional
    public void deleteDesignation(Long id) {
        designationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void logActivity(String action, String email, String details) {
        Employee emp = email != null ? employeeRepository.findByUser_Email(email).orElse(null) : null;
        SystemLog log = new SystemLog(
                null,
                action,
                emp,
                null,
                details
        );
        systemLogRepository.save(log);
    }

    @Override
    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAllByOrderByTimestampDesc();
    }
}
