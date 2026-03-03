package com.rev.app.service;

import com.rev.app.entity.HolidayCalendar;
import com.rev.app.repository.HolidayCalendarRepository;
import com.rev.app.service.HolidayServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HolidayServiceTest {

    private AutoCloseable closeable;

    @Mock
    private HolidayCalendarRepository holidayCalendarRepository;

    @InjectMocks
    private HolidayServiceImpl holidayService;

    private HolidayCalendar holiday;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        holiday = new HolidayCalendar();
        holiday.setHolidayId(1L);
        holiday.setHolidayDate(LocalDate.of(2023, 1, 1));
        holiday.setDescription("First day of the year");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenGetAllHolidays_thenReturnList() {
        when(holidayCalendarRepository.findAll()).thenReturn(Arrays.asList(holiday));
        List<HolidayCalendar> result = holidayService.getAllHolidays();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("First day of the year");
    }

    @Test
    public void whenSaveHoliday_thenReturnHoliday() {
        when(holidayCalendarRepository.save(any(HolidayCalendar.class))).thenReturn(holiday);
        HolidayCalendar result = holidayService.saveHoliday(holiday);
        assertThat(result.getDescription()).isEqualTo("First day of the year");
        verify(holidayCalendarRepository).save(any(HolidayCalendar.class));
    }

    @Test
    public void whenDeleteHoliday_thenCallRepository() {
        doNothing().when(holidayCalendarRepository).deleteById(anyLong());
        holidayService.deleteHoliday(1L);
        verify(holidayCalendarRepository).deleteById(1L);
    }

    @Test
    public void whenGetHolidayById_thenReturnOptional() {
        when(holidayCalendarRepository.findById(anyLong())).thenReturn(Optional.of(holiday));
        Optional<HolidayCalendar> result = holidayService.getHolidayById(1L);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getDescription()).isEqualTo("First day of the year");
    }
}
