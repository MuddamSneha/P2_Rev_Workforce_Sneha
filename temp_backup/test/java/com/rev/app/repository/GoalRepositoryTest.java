package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.Goal;
import com.rev.app.repository.GoalRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GoalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GoalRepository goalRepository;

    @Test
    public void whenSaveGoal_thenReturnGoal() {
        Goal goal = new Goal();
        goal.setGoalDesc("Test Goal");
        
        Goal saved = goalRepository.save(goal);

        assertThat(saved.getGoalId()).isNotNull();
        assertThat(saved.getGoalDesc()).isEqualTo("Test Goal");
    }

    @Test
    public void whenFindByEmployee_EmpId_thenReturnPage() {
        Employee emp = new Employee();
        emp.setEmpId("EMP001");
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setJoiningDate(java.time.LocalDate.now());
        entityManager.persist(emp);

        Goal goal = new Goal();
        goal.setGoalDesc("Emp Goal");
        goal.setEmployee(emp);
        entityManager.persist(goal);
        entityManager.flush();

        Page<Goal> result = goalRepository.findByEmployee_EmpId("EMP001", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGoalDesc()).isEqualTo("Emp Goal");
    }

    @Test
    public void whenFindByEmployee_Manager_EmpId_thenReturnPage() {
        Employee manager = new Employee();
        manager.setEmpId("MGR001");
        manager.setFirstName("Manager");
        manager.setLastName("User");
        manager.setJoiningDate(java.time.LocalDate.now());
        entityManager.persist(manager);

        Employee emp = new Employee();
        emp.setEmpId("EMP002");
        emp.setFirstName("Emp");
        emp.setLastName("User");
        emp.setJoiningDate(java.time.LocalDate.now());
        emp.setManager(manager);
        entityManager.persist(emp);

        Goal goal = new Goal();
        goal.setGoalDesc("Team Goal");
        goal.setEmployee(emp);
        entityManager.persist(goal);
        entityManager.flush();

        Page<Goal> result = goalRepository.findByEmployee_Manager_EmpId("MGR001", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGoalDesc()).isEqualTo("Team Goal");
    }
}
