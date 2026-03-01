package com.rev.app.dto;

import java.time.LocalDateTime;

public class AnnouncementDto {
    private Long announcementId;
    private String title;
    private String message;
    private String createdBy;
    private String creatorName;
    private LocalDateTime createdAt;

    public AnnouncementDto() {}

    public AnnouncementDto(Long announcementId, String title, String message, String createdBy, String creatorName, LocalDateTime createdAt) {
        this.announcementId = announcementId;
        this.title = title;
        this.message = message;
        this.createdBy = createdBy;
        this.creatorName = creatorName;
        this.createdAt = createdAt;
    }

    public Long getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(Long announcementId) { this.announcementId = announcementId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
