package com.rev.app.repository;

import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.entity.Employee;
import com.rev.app.entity.User;
import com.rev.app.repository.projection.EmployeeSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeRepositoryTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void whenFindByUser_UserId_thenReturnEmployee() {
        User user = new User();
        user.setEmail("test-user@example.com");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        user.setIsActive(1);
        user = entityManager.persist(user);

        Employee employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setJoiningDate(LocalDate.now());
        employee.setUser(user);
        entityManager.persist(employee);
        entityManager.flush();

        Optional<Employee> found = employeeRepository.findByUser_UserId(user.getUserId());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getEmpId()).isEqualTo("EMP001");
    }

    @Test
    public void whenFindByManagerId_thenReturnEmployees() {
        Employee manager = new Employee();
        manager.setEmpId("MGR001");
        manager.setFirstName("Manager");
        manager.setLastName("User");
        manager.setJoiningDate(LocalDate.now());
        entityManager.persist(manager);

        User user = new User();
        user.setEmail("emp@example.com");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        user.setIsActive(1);
        entityManager.persist(user);

        Employee employee = new Employee();
        employee.setEmpId("EMP002");
        employee.setFirstName("Employee");
        employee.setLastName("User");
        employee.setJoiningDate(LocalDate.now());
        employee.setManager(manager);
        employee.setUser(user);
        entityManager.persist(employee);
        entityManager.flush();

        List<Employee> results = employeeRepository.findByManagerId("MGR001");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmpId()).isEqualTo("EMP002");
    }

    @Test
    public void whenSearchEmployees_thenReturnPageOfEmployees() {
        Department dept = new Department();
        dept.setDepartmentName("Engineering");
        entityManager.persist(dept);

        Designation desig = new Designation();
        desig.setDesignationName("Developer");
        entityManager.persist(desig);

        Employee employee = new Employee();
        employee.setEmpId("EMP-SEARCH");
        employee.setFirstName("Jane");
        employee.setLastName("Search");
        employee.setJoiningDate(LocalDate.now());
        employee.setDepartment(dept);
        employee.setDesignation(desig);
        entityManager.persist(employee);
        entityManager.flush();

        Page<Employee> result = employeeRepository.searchEmployees("Jane", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    public void whenFindAllSummaries_thenReturnList() {
        Employee employee = new Employee();
        employee.setEmpId("EMP-SUM");
        employee.setFirstName("Summary");
        employee.setLastName("User");
        employee.setJoiningDate(LocalDate.now());
        entityManager.persist(employee);
        entityManager.flush();

        List<EmployeeSummary> summaries = employeeRepository.findAllSummaries();

        assertThat(summaries).isNotEmpty();
        assertThat(summaries.get(0).getFirstName()).isEqualTo("Summary");
    }
}
