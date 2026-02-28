package com.rev.app.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "holiday_calendar")
public class HolidayCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long holidayId;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(nullable = false, length = 100)
    private String description;

    public HolidayCalendar() {}

    public HolidayCalendar(Long holidayId, LocalDate holidayDate, String description) {
        this.holidayId = holidayId;
        this.holidayDate = holidayDate;
        this.description = description;
    }

    public Long getHolidayId() { return holidayId; }
    public void setHolidayId(Long holidayId) { this.holidayId = holidayId; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public void setHolidayDate(LocalDate holidayDate) { this.holidayDate = holidayDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
