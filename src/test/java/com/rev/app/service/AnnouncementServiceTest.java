package com.rev.app.service;

import com.rev.app.dto.AnnouncementDto;
import com.rev.app.entity.Announcement;
import com.rev.app.entity.Employee;
import com.rev.app.entity.User;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.AnnouncementRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.service.AnnouncementServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnnouncementServiceTest {

    private AutoCloseable closeable;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    private Announcement announcement;
    private AnnouncementDto announcementDto;
    private User user;
    private Employee employee;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("admin@example.com");

        employee = new Employee();
        employee.setEmpId("EMP001");
        employee.setUser(user);

        announcement = new Announcement();
        announcement.setAnnouncementId(1L);
        announcement.setTitle("Test Title");
        announcement.setMessage("Test Message");
        announcement.setCreatedBy(employee);

        announcementDto = new AnnouncementDto();
        announcementDto.setAnnouncementId(1L);
        announcementDto.setTitle("Test Title");
        announcementDto.setMessage("Test Message");
        announcementDto.setCreatedBy("admin@example.com");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenCreateAnnouncementWithEmail_thenReturnAnnouncementDto() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));
        when(employeeRepository.findByUser_UserId(anyLong())).thenReturn(Optional.of(employee));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);
        when(dtoMapper.toAnnouncementDto(any(Announcement.class))).thenReturn(announcementDto);

        AnnouncementDto created = announcementService.createAnnouncement(announcementDto);

        assertThat(created.getTitle()).isEqualTo("Test Title");
        verify(announcementRepository).save(any(Announcement.class));
    }

    @Test
    public void whenCreateAnnouncementWithEmpId_thenReturnAnnouncementDto() {
        announcementDto.setCreatedBy("EMP001");
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);
        when(dtoMapper.toAnnouncementDto(any(Announcement.class))).thenReturn(announcementDto);

        AnnouncementDto created = announcementService.createAnnouncement(announcementDto);

        assertThat(created.getTitle()).isEqualTo("Test Title");
        verify(announcementRepository).save(any(Announcement.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenCreateAnnouncementUserNotFound_thenThrowException() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        announcementService.createAnnouncement(announcementDto);
    }

    @Test
    public void whenGetAllAnnouncements_thenReturnPageOfDtos() {
        Page<Announcement> page = new PageImpl<>(Arrays.asList(announcement));
        when(announcementRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);
        when(dtoMapper.toAnnouncementDto(any(Announcement.class))).thenReturn(announcementDto);

        Page<AnnouncementDto> result = announcementService.getAllAnnouncements(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Title");
    }

    @Test
    public void whenDeleteAnnouncement_thenCallRepository() {
        doNothing().when(announcementRepository).deleteById(anyLong());

        announcementService.deleteAnnouncement(1L);

        verify(announcementRepository).deleteById(1L);
    }

    @Test
    public void whenUpdateAnnouncement_thenReturnUpdatedDto() {
        when(announcementRepository.findById(anyLong())).thenReturn(Optional.of(announcement));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);
        when(dtoMapper.toAnnouncementDto(any(Announcement.class))).thenReturn(announcementDto);

        AnnouncementDto updated = announcementService.updateAnnouncement(announcementDto);

        assertThat(updated.getTitle()).isEqualTo("Test Title");
        verify(announcementRepository).save(any(Announcement.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateAnnouncementNotFound_thenThrowException() {
        when(announcementRepository.findById(anyLong())).thenReturn(Optional.empty());

        announcementService.updateAnnouncement(announcementDto);
    }
}
