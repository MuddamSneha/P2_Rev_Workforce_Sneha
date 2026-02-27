package com.rev.app.service;

import com.rev.app.dto.NotificationDto;
import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String message, String type);

    List<NotificationDto> getUserNotifications(Long userId);
    List<NotificationDto> getAllNotifications();
    void markAsRead(Long notificationId);
}
