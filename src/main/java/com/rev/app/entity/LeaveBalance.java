package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_balance", uniqueConstraints = { @UniqueConstraint(columnNames = { "emp_id", "leave_type_id" }) })
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(name = "balance_days", nullable = false)
    private Integer balanceDays;

    public LeaveBalance() {}

    public LeaveBalance(Long balanceId, Employee employee, LeaveType leaveType, Integer balanceDays) {
        this.balanceId = balanceId;
        this.employee = employee;
        this.leaveType = leaveType;
        this.balanceDays = balanceDays;
    }

    public Long getBalanceId() { return balanceId; }
    public void setBalanceId(Long balanceId) { this.balanceId = balanceId; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }
    public Integer getBalanceDays() { return balanceDays; }
    public void setBalanceDays(Integer balanceDays) { this.balanceDays = balanceDays; }
}
