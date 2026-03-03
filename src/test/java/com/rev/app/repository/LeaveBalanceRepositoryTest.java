package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.LeaveBalance;
import com.rev.app.entity.LeaveType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LeaveBalanceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Test
    public void whenFindByEmployee_EmpId_thenReturnList() {
        Employee emp = new Employee();
        emp.setEmpId("EMP999");
        emp.setFirstName("First");
        emp.setLastName("Last");
        emp.setJoiningDate(java.time.LocalDate.now());
        entityManager.persist(emp);

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployee(emp);
        balance.setBalanceDays(10);
        entityManager.persist(balance);
        entityManager.flush();

        List<LeaveBalance> result = leaveBalanceRepository.findByEmployee_EmpId("EMP999");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBalanceDays()).isEqualTo(10);
    }

    @Test
    public void whenFindByEmployeeAndLeaveType_thenReturnOptional() {
        Employee emp = new Employee();
        emp.setEmpId("EMP001");
        emp.setFirstName("First");
        emp.setLastName("Last");
        emp.setJoiningDate(java.time.LocalDate.now());
        entityManager.persist(emp);

        LeaveType type = new LeaveType();
        type.setLeaveName("Sick");
        type.setMaxPerYear(10);
        entityManager.persist(type);

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployee(emp);
        balance.setLeaveType(type);
        balance.setBalanceDays(5);
        entityManager.persist(balance);
        entityManager.flush();

        Optional<LeaveBalance> found = leaveBalanceRepository.findByEmployee_EmpIdAndLeaveType_LeaveTypeId("EMP001", type.getLeaveTypeId());

        assertThat(found).isPresent();
        assertThat(found.get().getBalanceDays()).isEqualTo(5);
    }
}
