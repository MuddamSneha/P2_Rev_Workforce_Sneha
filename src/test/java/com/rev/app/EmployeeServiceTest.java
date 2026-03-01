package com.rev.app;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EmployeeServiceImpl.
 *
 * NOTE: DTOMapper is NOT mocked — it is a plain real instance injected via
 * ReflectionTestUtils. This avoids a Byte Buddy incompatibility on Java 25:
 * both @Mock and @Spy require Byte Buddy class instrumentation which is
 * unsupported for Java 25 in the version bundled with Spring Boot 3.4.3.
 * DTOMapper has no injected dependencies, so a real instance is perfectly safe.
 */
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl();
        // Inject mocked repository and real DTOMapper (no Byte Buddy instrumentation needed)
        ReflectionTestUtils.setField(employeeService, "employeeRepository", employeeRepository);
        ReflectionTestUtils.setField(employeeService, "dtoMapper", new DTOMapper());
    }

    @Test
    void testGetAllEmployeesPaginated() {
        // Arrange
        Employee emp1 = new Employee();
        emp1.setEmpId("E01");
        emp1.setFirstName("Alice");

        Employee emp2 = new Employee();
        emp2.setEmpId("E02");
        emp2.setFirstName("Bob");

        List<Employee> employeeList = Arrays.asList(emp1, emp2);
        Page<Employee> employeePage = new PageImpl<>(employeeList);

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);

        // Act
        Page<EmployeeDto> result = employeeService.getAllEmployees(0, 10, "firstName");

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Alice", result.getContent().get(0).getFirstName());
        assertEquals("Bob", result.getContent().get(1).getFirstName());

        // Verify correct PageRequest (with sorting) was used
        verify(employeeRepository).findAll(PageRequest.of(0, 10, Sort.by("firstName")));
    }
}
