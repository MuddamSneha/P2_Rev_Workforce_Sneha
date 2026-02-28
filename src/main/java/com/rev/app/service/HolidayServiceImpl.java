package com.rev.app.service;

import com.rev.app.entity.HolidayCalendar;
import com.rev.app.repository.HolidayCalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayCalendarRepository holidayCalendarRepository;

    @Override
    public List<HolidayCalendar> getAllHolidays() {
        return holidayCalendarRepository.findAll();
    }

    @Override
    public HolidayCalendar saveHoliday(HolidayCalendar holiday) {
        return holidayCalendarRepository.save(holiday);
    }

    @Override
    public void deleteHoliday(Long id) {
        holidayCalendarRepository.deleteById(id);
    }

    @Override
    public java.util.Optional<HolidayCalendar> getHolidayById(Long id) {
        return holidayCalendarRepository.findById(id);
    }
}
