package com.rev.app;

import com.rev.app.entity.Department;
import com.rev.app.entity.Employee;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.projection.EmployeeSummary;
import com.rev.app.repository.specification.EmployeeSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        Department it = new Department(null, "IT");
        departmentRepository.save(it);

        Department hr = new Department(null, "HR");
        departmentRepository.save(hr);

        Employee e1 = new Employee("EMP001", null, "John", "Doe", "1234567890", "Address 1", "9876543210", 
                LocalDate.of(1990, 1, 1), LocalDate.now(), it, null, null, new BigDecimal("50000"), "SSN1");
        Employee e2 = new Employee("EMP002", null, "Jane", "Smith", "0987654321", "Address 2", "1234567890", 
                LocalDate.of(1992, 2, 2), LocalDate.now(), it, null, null, new BigDecimal("60000"), "SSN2");
        Employee e3 = new Employee("EMP003", null, "Bob", "Wilson", "1122334455", "Address 3", "5544332211", 
                LocalDate.of(1985, 3, 3), LocalDate.now(), hr, null, null, new BigDecimal("55000"), "SSN3");

        employeeRepository.saveAll(List.of(e1, e2, e3));
    }

    @Test
    void testFilterByDepartment() {
        Specification<Employee> spec = EmployeeSpecification.filterEmployees("IT", null, null);
        List<Employee> results = employeeRepository.findAll(spec);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Employee::getFirstName).containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void testSearchByName() {
        Specification<Employee> spec = EmployeeSpecification.filterEmployees(null, null, "Jane");
        List<Employee> results = employeeRepository.findAll(spec);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testProjection() {
        List<EmployeeSummary> summaries = employeeRepository.findAllSummaries();
        assertThat(summaries).hasSize(3);
        assertThat(summaries.get(0).getEmpId()).isNotNull();
        assertThat(summaries.get(0).getFirstName()).isNotNull();
        assertThat(summaries.get(0).getLastName()).isNotNull();
        // Check that full entity wasn't unnecessarily fetched if we had a deep tree
    }
}
