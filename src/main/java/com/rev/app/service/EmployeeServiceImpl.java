package com.rev.app.service;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.entity.User;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.repository.projection.EmployeeSummary;
import com.rev.app.repository.specification.EmployeeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        if (employeeRepository.existsById(dto.getEmpId())) {
            throw new com.rev.app.exceptions.BusinessException("Employee with ID " + dto.getEmpId() + " already exists.");
        }
        String password = (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) ? dto.getPassword() : "Welcome@123";
        User user = userService.createUser(dto.getEmail(), password, User.Role.valueOf(dto.getRole()));

        Department dept = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
        Employee manager = dto.getManagerId() != null ? employeeRepository.findById(dto.getManagerId()).orElse(null)
                : null;

        Designation designation = dto.getDesignationId() != null ? 
                designationRepository.findById(dto.getDesignationId()).orElse(null) : null;

        Employee employee = new Employee(
                dto.getEmpId(),
                user,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getEmergencyContact(),
                dto.getDob(),
                dto.getJoiningDate(),
                dept,
                designation,
                manager,
                dto.getSalary(),
                dto.getSsId()
        );

        Employee saved = employeeRepository.save(employee);
        leaveService.initializeLeaveBalances(saved.getEmpId());
        
        return dtoMapper.toEmployeeDto(saved);
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(EmployeeDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmpId()));

        if (dto.getFirstName() != null) employee.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) employee.setLastName(dto.getLastName());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());
        employee.setEmergencyContact(dto.getEmergencyContact());
        if (dto.getDesignationId() != null) {
            employee.setDesignation(designationRepository.findById(dto.getDesignationId()).orElse(null));
        }
        employee.setSalary(dto.getSalary());
        employee.setDob(dto.getDob());
        if (dto.getJoiningDate() != null) {
            employee.setJoiningDate(dto.getJoiningDate());
        }

        if (dto.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(dto.getDepartmentId()).orElse(null));
        }

        if (dto.getManagerId() != null && !dto.getManagerId().isEmpty()) {
            employee.setManager(employeeRepository.findById(dto.getManagerId()).orElse(null));
        } else {
            employee.setManager(null);
        }

        if (dto.getRole() != null && employee.getUser() != null) {
            employee.getUser().setRole(User.Role.valueOf(dto.getRole()));
        }

        return dtoMapper.toEmployeeDto(employeeRepository.save(employee));
    }

    @Override
    public EmployeeDto getEmployeeById(String empId) {
        return employeeRepository.findById(empId)
                .map(dtoMapper::toEmployeeDto)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));
    }

    @Override
    public EmployeeDto getEmployeeByUserId(Long userId) {
        return employeeRepository.findByUser_UserId(userId)
                .map(dtoMapper::toEmployeeDto)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for User ID: " + userId));
    }

    @Override
    public org.springframework.data.domain.Page<EmployeeDto> getAllEmployees(int page, int size, String sortBy) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
        return employeeRepository.findAll(pageable)
                .map(dtoMapper::toEmployeeDto);
    }

    @Override
    public org.springframework.data.domain.Page<EmployeeDto> searchEmployees(String query, int page, int size, String sortBy) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
        return employeeRepository.searchEmployees(query, pageable)
                .map(dtoMapper::toEmployeeDto);
    }

    @Override
    public List<EmployeeDto> getDirectReports(String managerId) {
        return employeeRepository.findByManagerId(managerId).stream()
                .map(dtoMapper::toEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployee(String empId) {
        employeeRepository.findById(empId).ifPresent(emp -> {
            User user = emp.getUser();
            if (user != null) {
                user.setIsActive(0);
                userRepository.save(user);
            }
        });
    }

    @Override
    @Transactional
    public void reactivateEmployee(String empId) {
        employeeRepository.findById(empId).ifPresent(emp -> {
            User user = emp.getUser();
            if (user != null) {
                user.setIsActive(1);
                userRepository.save(user);
            }
        });
    }

    @Override
    @Transactional
    public EmployeeDto updateProfile(String empId, EmployeeDto dto) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));

        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());
        employee.setEmergencyContact(dto.getEmergencyContact());


        return dtoMapper.toEmployeeDto(employeeRepository.save(employee));
    }

    @Override
    public boolean isEmployeeExists(String empId) {
        return employeeRepository.existsById(empId);
    }

    @Override
    public List<EmployeeSummary> getEmployeeSummaries() {
        return employeeRepository.findAllSummaries();
    }

    @Override
    public List<EmployeeDto> filterEmployees(String department, String designation, String search) {
        return employeeRepository.findAll(EmployeeSpecification.filterEmployees(department, designation, search))
                .stream()
                .map(dtoMapper::toEmployeeDto)
                .collect(Collectors.toList());
    }
}
