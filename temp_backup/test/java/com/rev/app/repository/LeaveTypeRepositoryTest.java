package com.rev.app.repository;

import com.rev.app.entity.LeaveType;
import com.rev.app.repository.LeaveTypeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LeaveTypeRepositoryTest {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Test
    public void whenSaveLeaveType_thenReturnLeaveType() {
        LeaveType type = new LeaveType();
        type.setLeaveName("Annual");
        type.setMaxPerYear(20);
        
        LeaveType saved = leaveTypeRepository.save(type);

        assertThat(saved.getLeaveTypeId()).isNotNull();
        assertThat(saved.getLeaveName()).isEqualTo("Annual");
    }

    @Test
    public void whenFindById_thenReturnLeaveType() {
        LeaveType type = new LeaveType();
        type.setLeaveName("Sick");
        type.setMaxPerYear(10);
        leaveTypeRepository.save(type);

        LeaveType found = leaveTypeRepository.findById(type.getLeaveTypeId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getLeaveName()).isEqualTo("Sick");
    }
}
