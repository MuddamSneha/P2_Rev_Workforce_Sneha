package com.rev.app.repository;

import com.rev.app.entity.Notification;
import com.rev.app.entity.User;
import com.rev.app.repository.NotificationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void whenFindByUserId_thenReturnSortedList() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        entityManager.persist(user);

        Notification n1 = new Notification();
        n1.setUser(user);
        n1.setMessage("First");
        entityManager.persist(n1);

        Notification n2 = new Notification();
        n2.setUser(user);
        n2.setMessage("Second");
        entityManager.persist(n2);

        entityManager.flush();

        List<Notification> result = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

        assertThat(result).hasSize(2);
    }

    @Test
    public void whenCountByUserId_thenReturnCount() {
        User user = new User();
        user.setEmail("count@example.com");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        entityManager.persist(user);

        Notification n = new Notification();
        n.setUser(user);
        n.setMessage("Test");
        entityManager.persist(n);
        entityManager.flush();

        long count = notificationRepository.countByUser_UserId(user.getUserId());

        assertThat(count).isEqualTo(1);
    }
}
