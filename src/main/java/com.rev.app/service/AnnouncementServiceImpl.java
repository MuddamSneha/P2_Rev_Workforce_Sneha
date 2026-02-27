package com.rev.app.service;

import com.rev.app.dto.AnnouncementDto;
import com.rev.app.entity.Announcement;
import com.rev.app.entity.Employee;
import com.rev.app.mapper.DTOMapper;
import com.rev.app.repository.AnnouncementRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Override
    @Transactional
    public AnnouncementDto createAnnouncement(AnnouncementDto dto) {
        Employee emp = employeeRepository.findById(dto.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getCreatedBy()));

        Announcement announcement = new Announcement(
                null,
                dto.getTitle(),
                dto.getMessage(),
                emp,
                null
        );

        return dtoMapper.toAnnouncementDto(announcementRepository.save(announcement));
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(dtoMapper::toAnnouncementDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        announcementRepository.deleteById(announcementId);
    }

    @Override
    @Transactional
    public AnnouncementDto updateAnnouncement(AnnouncementDto dto) {
        Announcement announcement = announcementRepository.findById(dto.getAnnouncementId())
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found with ID: " + dto.getAnnouncementId()));
        announcement.setTitle(dto.getTitle());
        announcement.setMessage(dto.getMessage());
        return dtoMapper.toAnnouncementDto(announcementRepository.save(announcement));
    }
}
