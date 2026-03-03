package com.rev.app.service;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.Department;
import com.rev.app.entity.Employee;
import com.rev.app.entity.User;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.service.EmployeeServiceImpl;
import com.rev.app.service.LeaveService;
import com.rev.app.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    private AutoCloseable closeable;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private DTOMapper dtoMapper;

    @Mock
    private LeaveService leaveService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;
    private User user;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = MockitoAnnotations.openMocks(this);
        
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setRole(User.Role.ROLE_EMPLOYEE);

        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setUser(user);

        employeeDto = new EmployeeDto();
        employeeDto.setEmpId("EMP001");
        employeeDto.setFirstName("John");
        employeeDto.setLastName("Doe");
        employeeDto.setEmail("test@example.com");
        employeeDto.setRole("ROLE_EMPLOYEE");
        employeeDto.setDepartmentId(1L);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenCreateEmployee_thenReturnEmployeeDto() {
        when(employeeRepository.existsById(anyString())).thenReturn(false);
        when(userService.createUser(anyString(), anyString(), any())).thenReturn(user);
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(new Department()));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(dtoMapper.toEmployeeDto(any(Employee.class))).thenReturn(employeeDto);

        EmployeeDto created = employeeService.createEmployee(employeeDto);

        assertThat(created.getEmpId()).isEqualTo("EMP001");
        verify(employeeRepository).save(any(Employee.class));
        verify(leaveService).initializeLeaveBalances("EMP001");
    }

    @Test(expected = com.rev.app.exceptions.BusinessException.class)
    public void whenCreateExistingEmployee_thenThrowException() {
        when(employeeRepository.existsById(anyString())).thenReturn(true);
        employeeService.createEmployee(employeeDto);
    }

    @Test
    public void whenGetEmployeeById_thenReturnEmployeeDto() {
        when(employeeRepository.findById("EMP001")).thenReturn(Optional.of(employee));
        when(dtoMapper.toEmployeeDto(employee)).thenReturn(employeeDto);

        EmployeeDto found = employeeService.getEmployeeById("EMP001");

        assertThat(found.getEmpId()).isEqualTo("EMP001");
    }

    @Test
    public void whenDeleteEmployee_thenDeactivateUser() {
        when(employeeRepository.findById("EMP001")).thenReturn(Optional.of(employee));
        employeeService.deleteEmployee("EMP001");

        assertThat(user.getIsActive()).isEqualTo(0);
        verify(userRepository).save(user);
    }
}
