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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DTOMapper dtoMapper;

    @Override
    @Transactional
    public AnnouncementDto createAnnouncement(AnnouncementDto dto) {
        // createdBy is the admin's email — resolve to Employee via User
        String createdByEmail = dto.getCreatedBy();
        Employee emp = null;
        if (createdByEmail != null && !createdByEmail.isBlank()) {
            // Check if it looks like an email (contains @) or an empId
            if (createdByEmail.contains("@")) {
                User user = userRepository.findByEmailIgnoreCase(createdByEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + createdByEmail));
                emp = employeeRepository.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee record not found for user: " + createdByEmail));
            } else {
                emp = employeeRepository.findById(createdByEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + createdByEmail));
            }
        }

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
    public org.springframework.data.domain.Page<AnnouncementDto> getAllAnnouncements(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return announcementRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(dtoMapper::toAnnouncementDto);
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
