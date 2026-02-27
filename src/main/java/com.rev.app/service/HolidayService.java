package com.rev.app.service;

import com.rev.app.entity.HolidayCalendar;
import java.util.List;

public interface HolidayService {
    List<HolidayCalendar> getAllHolidays();

    HolidayCalendar saveHoliday(HolidayCalendar holiday);

    void deleteHoliday(Long id);

    java.util.Optional<HolidayCalendar> getHolidayById(Long id);
}
