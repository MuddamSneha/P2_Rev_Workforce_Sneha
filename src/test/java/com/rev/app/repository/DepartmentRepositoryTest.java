package com.rev.app.repository;

import com.rev.app.entity.Department;
import com.rev.app.repository.DepartmentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void whenSaveDepartment_thenReturnDepartment() {
        Department dept = new Department();
        dept.setDepartmentName("HR");
        
        Department saved = departmentRepository.save(dept);

        assertThat(saved.getDepartmentId()).isNotNull();
        assertThat(saved.getDepartmentName()).isEqualTo("HR");
    }

    @Test
    public void whenFindById_thenReturnDepartment() {
        Department dept = new Department();
        dept.setDepartmentName("IT");
        departmentRepository.save(dept);

        Department found = departmentRepository.findById(dept.getDepartmentId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getDepartmentName()).isEqualTo("IT");
    }

    @Test
    public void whenDeleteDepartment_thenDeleted() {
        Department dept = new Department();
        dept.setDepartmentName("Sales");
        departmentRepository.save(dept);

        departmentRepository.delete(dept);

        assertThat(departmentRepository.findById(dept.getDepartmentId())).isEmpty();
    }
}
