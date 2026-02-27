package com.rev.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "goal_desc", nullable = false, length = 500)
    private String goalDesc;

    private LocalDate deadline;

    @Column(length = 10)
    private String priority = "MEDIUM"; // HIGH, MEDIUM, LOW

    @Column(name = "success_metric")
    private String successMetric;

    private Integer progress = 0;

    @Column(length = 20)
    private String status = "NOT_STARTED";

    @Column(name = "manager_comment", length = 500)
    private String managerComment;

    public Goal() {}

    public Goal(Long goalId, Employee employee, String goalDesc, LocalDate deadline, String priority, String successMetric, Integer progress, String status, String managerComment) {
        this.goalId = goalId;
        this.employee = employee;
        this.goalDesc = goalDesc;
        this.deadline = deadline;
        this.priority = priority;
        this.successMetric = successMetric;
        this.progress = progress;
        this.status = status;
        this.managerComment = managerComment;
    }

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public String getGoalDesc() { return goalDesc; }
    public void setGoalDesc(String goalDesc) { this.goalDesc = goalDesc; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getSuccessMetric() { return successMetric; }
    public void setSuccessMetric(String successMetric) { this.successMetric = successMetric; }
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getManagerComment() { return managerComment; }
    public void setManagerComment(String managerComment) { this.managerComment = managerComment; }
}
