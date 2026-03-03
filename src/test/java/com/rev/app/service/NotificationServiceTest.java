package com.rev.app.service;

import com.rev.app.dto.NotificationDto;
import com.rev.app.entity.Notification;
import com.rev.app.entity.User;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.NotificationRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.service.NotificationServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private AutoCloseable closeable;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;
    private NotificationDto notificationDto;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setUser(user);
        notification.setMessage("Test message");
        notification.setType("INFO");
        notification.setIsRead(0);

        notificationDto = new NotificationDto();
        notificationDto.setNotificationId(1L);
        notificationDto.setMessage("Test message");
        notificationDto.setType("INFO");
        notificationDto.setIsRead(0);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenSendNotificationUserFound_thenSaveNotification() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(1L, "Test message", "INFO");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    public void whenSendNotificationUserNotFound_thenDoNothing() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        notificationService.sendNotification(1L, "Test message", "INFO");

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void whenGetUserNotifications_thenReturnList() {
        when(notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(anyLong())).thenReturn(Arrays.asList(notification));
        when(dtoMapper.toNotificationDto(any(Notification.class))).thenReturn(notificationDto);

        List<NotificationDto> result = notificationService.getUserNotifications(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("Test message");
    }

    @Test
    public void whenGetAllNotifications_thenReturnList() {
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(notification));
        when(dtoMapper.toNotificationDto(any(Notification.class))).thenReturn(notificationDto);

        List<NotificationDto> result = notificationService.getAllNotifications();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("Test message");
    }

    @Test
    public void whenMarkAsRead_thenUpdateAndSave() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L);

        assertThat(notification.getIsRead()).isEqualTo(1);
        verify(notificationRepository).save(any(Notification.class));
    }
}
