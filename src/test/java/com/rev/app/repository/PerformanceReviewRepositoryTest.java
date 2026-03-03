package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceReview;
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
public class PerformanceReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    @Test
    public void whenFindByEmployee_EmpId_thenReturnPage() {
        Employee emp = new Employee();
        emp.setEmpId("EMP001");
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setJoiningDate(java.time.LocalDate.now());
        entityManager.persist(emp);

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(emp);
        review.setReviewYear(2023);
        review.setManagerFeedback("Good");
        entityManager.persist(review);
        entityManager.flush();

        Page<PerformanceReview> result = performanceReviewRepository.findByEmployee_EmpId("EMP001", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
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
        emp.setFirstName("Employee");
        emp.setLastName("User");
        emp.setJoiningDate(java.time.LocalDate.now());
        emp.setManager(manager);
        entityManager.persist(emp);

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(emp);
        review.setReviewYear(2023);
        entityManager.persist(review);
        entityManager.flush();

        Page<PerformanceReview> result = performanceReviewRepository.findByEmployee_Manager_EmpId("MGR001", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }
}
