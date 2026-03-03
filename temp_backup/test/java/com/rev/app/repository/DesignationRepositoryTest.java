package com.rev.app.repository;

import com.rev.app.entity.Designation;
import com.rev.app.repository.DesignationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DesignationRepositoryTest {

    @Autowired
    private DesignationRepository designationRepository;

    @Test
    public void whenSaveDesignation_thenReturnDesignation() {
        Designation desig = new Designation();
        desig.setDesignationName("Manager");
        
        Designation saved = designationRepository.save(desig);

        assertThat(saved.getDesignationId()).isNotNull();
        assertThat(saved.getDesignationName()).isEqualTo("Manager");
    }

    @Test
    public void whenFindByDesignationName_thenReturnDesignation() {
        Designation desig = new Designation();
        desig.setDesignationName("Developer");
        designationRepository.save(desig);

        Optional<Designation> found = designationRepository.findByDesignationName("Developer");

        assertThat(found).isPresent();
        assertThat(found.get().getDesignationName()).isEqualTo("Developer");
    }
}
