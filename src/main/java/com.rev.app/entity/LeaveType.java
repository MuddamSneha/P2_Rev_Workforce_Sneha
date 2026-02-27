package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_type")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveTypeId;

    @Column(name = "leave_name", nullable = false, length = 50)
    private String leaveName;

    @Column(name = "max_per_year", nullable = false)
    private Integer maxPerYear;

    public LeaveType() {}

    public LeaveType(Long leaveTypeId, String leaveName, Integer maxPerYear) {
        this.leaveTypeId = leaveTypeId;
        this.leaveName = leaveName;
        this.maxPerYear = maxPerYear;
    }

    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }
    public String getLeaveName() { return leaveName; }
    public void setLeaveName(String leaveName) { this.leaveName = leaveName; }
    public Integer getMaxPerYear() { return maxPerYear; }
    public void setMaxPerYear(Integer maxPerYear) { this.maxPerYear = maxPerYear; }
}
