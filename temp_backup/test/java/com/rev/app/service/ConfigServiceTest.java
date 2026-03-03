package com.rev.app.service;

import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.entity.SystemLog;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.SystemLogRepository;
import com.rev.app.service.ConfigServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {

    private AutoCloseable closeable;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private SystemLogRepository systemLogRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ConfigServiceImpl configService;

    private Department department;
    private Designation designation;
    private Employee employee;
    private SystemLog systemLog;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        department = new Department(1L, "Engineering");
        designation = new Designation(1L, "Software Engineer");

        employee = new Employee();
        employee.setEmpId("EMP001");

        systemLog = new SystemLog(1L, "LOGIN", employee, null, "User logged in");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenGetAllDepartments_thenReturnList() {
        when(departmentRepository.findAll()).thenReturn(Arrays.asList(department));
        List<Department> result = configService.getAllDepartments();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("Engineering");
    }

    @Test
    public void whenSaveDepartment_thenReturnDepartment() {
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        Department result = configService.saveDepartment(department);
        assertThat(result.getDepartmentName()).isEqualTo("Engineering");
    }

    @Test
    public void whenDeleteDepartment_thenCallRepository() {
        doNothing().when(departmentRepository).deleteById(anyLong());
        configService.deleteDepartment(1L);
        verify(departmentRepository).deleteById(1L);
    }

    @Test
    public void whenGetAllDesignations_thenReturnList() {
        when(designationRepository.findAll()).thenReturn(Arrays.asList(designation));
        List<Designation> result = configService.getAllDesignations();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDesignationName()).isEqualTo("Software Engineer");
    }

    @Test
    public void whenSaveDesignation_thenReturnDesignation() {
        when(designationRepository.save(any(Designation.class))).thenReturn(designation);
        Designation result = configService.saveDesignation(designation);
        assertThat(result.getDesignationName()).isEqualTo("Software Engineer");
    }

    @Test
    public void whenDeleteDesignation_thenCallRepository() {
        doNothing().when(designationRepository).deleteById(anyLong());
        configService.deleteDesignation(1L);
        verify(designationRepository).deleteById(1L);
    }

    @Test
    public void whenLogActivityWithEmail_thenSaveLog() {
        when(employeeRepository.findByUser_Email(anyString())).thenReturn(Optional.of(employee));
        when(systemLogRepository.save(any(SystemLog.class))).thenReturn(systemLog);

        configService.logActivity("TEST_ACTION", "test@example.com", "Testing action directly");

        verify(systemLogRepository).save(any(SystemLog.class));
    }

    @Test
    public void whenLogActivityWithoutEmail_thenSaveLog() {
        when(systemLogRepository.save(any(SystemLog.class))).thenReturn(systemLog);

        configService.logActivity("TEST_ACTION", null, "Testing action directly");

        verify(systemLogRepository).save(any(SystemLog.class));
    }

    @Test
    public void whenGetAllLogs_thenReturnListOrdered() {
        when(systemLogRepository.findAllByOrderByTimestampDesc()).thenReturn(Arrays.asList(systemLog));
        List<SystemLog> result = configService.getAllLogs();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAction()).isEqualTo("LOGIN");
    }
}
