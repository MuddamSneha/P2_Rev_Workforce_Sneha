package com.rev.app.repository;

import com.rev.app.entity.HolidayCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayCalendarRepository extends JpaRepository<HolidayCalendar, Long> {
    List<HolidayCalendar> findAllByOrderByHolidayDateAsc();
}
