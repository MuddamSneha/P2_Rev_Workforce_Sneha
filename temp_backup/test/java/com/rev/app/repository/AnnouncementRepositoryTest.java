package com.rev.app.repository;

import com.rev.app.entity.Announcement;
import com.rev.app.entity.Employee;
import com.rev.app.repository.AnnouncementRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AnnouncementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Test
    public void whenSaveAnnouncement_thenReturnAnnouncement() {
        Employee emp = new Employee();
        emp.setEmpId("ADMIN001");
        emp.setFirstName("Admin");
        emp.setLastName("User");
        emp.setJoiningDate(java.time.LocalDate.now());
        entityManager.persist(emp);

        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setMessage("Test Message");
        announcement.setCreatedBy(emp);
        announcement.setCreatedAt(LocalDateTime.now());

        Announcement saved = announcementRepository.save(announcement);

        assertThat(saved.getAnnouncementId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Title");
    }

    @Test
    public void whenFindAllByOrderByCreatedAtDesc_thenReturnSortedPage() {
        Announcement a1 = new Announcement();
        a1.setTitle("First");
        a1.setMessage("Content 1");
        a1.setCreatedAt(LocalDateTime.now().minusDays(1));
        entityManager.persist(a1);

        Announcement a2 = new Announcement();
        a2.setTitle("Second");
        a2.setMessage("Content 2");
        a2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(a2);

        entityManager.flush();

        Page<Announcement> result = announcementRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Second");
    }
}
