package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.LeaveApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LeaveApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Test
    public void whenSaveLeaveApplication_thenReturnLeaveApplication() {
        LeaveApplication leave = new LeaveApplication();
        leave.setReason("Fever");
        leave.setStartDate(LocalDate.now());
        leave.setEndDate(LocalDate.now().plusDays(1));
        leave.setStatus("PENDING");
        
        LeaveApplication saved = leaveApplicationRepository.save(leave);

        assertThat(saved.getLeaveId()).isNotNull();
        assertThat(saved.getReason()).isEqualTo("Fever");
    }

    @Test
    public void whenFindByEmployee_EmpId_thenReturnPage() {
        Employee emp = new Employee();
        emp.setEmpId("EMP123");
        emp.setFirstName("Jane");
        emp.setLastName("Doe");
        emp.setJoiningDate(LocalDate.now());
        entityManager.persist(emp);

        LeaveApplication la = new LeaveApplication();
        la.setEmployee(emp);
        la.setStartDate(LocalDate.now());
        la.setEndDate(LocalDate.now().plusDays(1));
        la.setStatus("APPROVED");
        entityManager.persist(la);
        entityManager.flush();

        Page<LeaveApplication> result = leaveApplicationRepository.findByEmployee_EmpId("EMP123", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    public void whenFindPendingByManagerId_thenReturnPage() {
        Employee manager = new Employee();
        manager.setEmpId("MGR789");
        manager.setFirstName("Manager");
        manager.setLastName("User");
        manager.setJoiningDate(LocalDate.now());
        entityManager.persist(manager);

        Employee emp = new Employee();
        emp.setEmpId("EMP456");
        emp.setFirstName("Employee");
        emp.setLastName("User");
        emp.setJoiningDate(LocalDate.now());
        emp.setManager(manager);
        entityManager.persist(emp);

        LeaveApplication la = new LeaveApplication();
        la.setEmployee(emp);
        la.setStartDate(LocalDate.now());
        la.setEndDate(LocalDate.now().plusDays(1));
        la.setStatus("PENDING");
        entityManager.persist(la);

        LeaveApplication approvedLa = new LeaveApplication();
        approvedLa.setEmployee(emp);
        approvedLa.setStartDate(LocalDate.now().minusDays(5));
        approvedLa.setEndDate(LocalDate.now().minusDays(4));
        approvedLa.setStatus("APPROVED");
        entityManager.persist(approvedLa);

        entityManager.flush();

        Page<LeaveApplication> result = leaveApplicationRepository.findPendingByManagerId("MGR789", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    public void whenSearchLeaves_thenReturnFilteredPage() {
        Employee emp = new Employee();
        emp.setEmpId("EMP001");
        emp.setFirstName("Jane");
        emp.setLastName("Doe");
        emp.setJoiningDate(LocalDate.now());
        entityManager.persist(emp);

        LeaveApplication la = new LeaveApplication();
        la.setEmployee(emp);
        la.setStartDate(LocalDate.now());
        la.setEndDate(LocalDate.now().plusDays(1));
        la.setStatus("PENDING");
        entityManager.persist(la);
        entityManager.flush();

        Page<LeaveApplication> result = leaveApplicationRepository.searchLeaves("EMP001", null, "PENDING", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }
}
