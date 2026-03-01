package com.rev.app.dto;

import java.time.LocalDate;

public class GoalDto {
    private Long goalId;
    private String empId;
    private String goalDesc;
    private LocalDate deadline;
    private String priority;
    private String successMetric;
    private Integer progress;
    private String status;
    private String managerComment;

    public GoalDto() {}

    public GoalDto(Long goalId, String empId, String goalDesc, LocalDate deadline, String priority, String successMetric, Integer progress, String status, String managerComment) {
        this.goalId = goalId;
        this.empId = empId;
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
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
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
