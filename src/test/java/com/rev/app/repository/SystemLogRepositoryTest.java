package com.rev.app.repository;

import com.rev.app.entity.SystemLog;
import com.rev.app.repository.SystemLogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SystemLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Test
    public void whenFindAllByOrderByTimestampDesc_thenReturnSortedList() {
        SystemLog s1 = new SystemLog();
        s1.setAction("Old");
        s1.setTimestamp(LocalDateTime.now().minusHours(1));
        entityManager.persist(s1);

        SystemLog s2 = new SystemLog();
        s2.setAction("New");
        s2.setTimestamp(LocalDateTime.now());
        entityManager.persist(s2);

        entityManager.flush();

        List<SystemLog> result = systemLogRepository.findAllByOrderByTimestampDesc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAction()).isEqualTo("New");
    }
}
