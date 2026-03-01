package com.rev.app.dto;

import java.math.BigDecimal;

public class PerformanceReviewDto {
    private Long reviewId;
    private String empId;
    private String employeeName;
    private Integer reviewYear;
    private String achievements;
    private String improvements;
    private String keyDeliverables;
    private BigDecimal selfRating;
    private BigDecimal managerRating;
    private String managerFeedback;
    private String status;

    public PerformanceReviewDto() {}

    public PerformanceReviewDto(Long reviewId, String empId, String employeeName, Integer reviewYear, String achievements, String improvements, String keyDeliverables, BigDecimal selfRating, BigDecimal managerRating, String managerFeedback, String status) {
        this.reviewId = reviewId;
        this.empId = empId;
        this.employeeName = employeeName;
        this.reviewYear = reviewYear;
        this.achievements = achievements;
        this.improvements = improvements;
        this.keyDeliverables = keyDeliverables;
        this.selfRating = selfRating;
        this.managerRating = managerRating;
        this.managerFeedback = managerFeedback;
        this.status = status;
    }

    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public Integer getReviewYear() { return reviewYear; }
    public void setReviewYear(Integer reviewYear) { this.reviewYear = reviewYear; }
    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }
    public String getImprovements() { return improvements; }
    public void setImprovements(String improvements) { this.improvements = improvements; }
    public String getKeyDeliverables() { return keyDeliverables; }
    public void setKeyDeliverables(String keyDeliverables) { this.keyDeliverables = keyDeliverables; }
    public BigDecimal getSelfRating() { return selfRating; }
    public void setSelfRating(BigDecimal selfRating) { this.selfRating = selfRating; }
    public BigDecimal getManagerRating() { return managerRating; }
    public void setManagerRating(BigDecimal managerRating) { this.managerRating = managerRating; }
    public String getManagerFeedback() { return managerFeedback; }
    public void setManagerFeedback(String managerFeedback) { this.managerFeedback = managerFeedback; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
