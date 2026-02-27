package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @CreationTimestamp
    @Column(name = "event_date", updatable = false)
    private LocalDateTime eventDate;

    public Event() {}

    public Event(Long eventId, Employee employee, String eventType, LocalDateTime eventDate) {
        this.eventId = eventId;
        this.employee = employee;
        this.eventType = eventType;
        this.eventDate = eventDate;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
}
