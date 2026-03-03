package com.rev.app.repository;

import com.rev.app.entity.HolidayCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class HolidayCalendarRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HolidayCalendarRepository holidayCalendarRepository;

    @Test
    public void whenSaveHoliday_thenReturnHoliday() {
        HolidayCalendar holiday = new HolidayCalendar();
        holiday.setDescription("New Year");
        holiday.setHolidayDate(LocalDate.of(2024, 1, 1));
        
        HolidayCalendar saved = holidayCalendarRepository.save(holiday);

        assertThat(saved.getHolidayId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("New Year");
    }

    @Test
    public void whenFindAllByOrderByHolidayDateAsc_thenReturnSortedList() {
        HolidayCalendar h1 = new HolidayCalendar();
        h1.setDescription("Later");
        h1.setHolidayDate(LocalDate.of(2024, 12, 25));
        entityManager.persist(h1);

        HolidayCalendar h2 = new HolidayCalendar();
        h2.setDescription("Earlier");
        h2.setHolidayDate(LocalDate.of(2024, 1, 1));
        entityManager.persist(h2);

        entityManager.flush();

        List<HolidayCalendar> result = holidayCalendarRepository.findAllByOrderByHolidayDateAsc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Earlier");
    }
}
