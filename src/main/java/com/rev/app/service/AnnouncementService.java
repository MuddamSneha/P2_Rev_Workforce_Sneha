package com.rev.app.service;

import com.rev.app.dto.AnnouncementDto;

public interface AnnouncementService {
    AnnouncementDto createAnnouncement(AnnouncementDto announcementDto);

    org.springframework.data.domain.Page<AnnouncementDto> getAllAnnouncements(int page, int size);

    void deleteAnnouncement(Long announcementId);

    AnnouncementDto updateAnnouncement(AnnouncementDto announcementDto);
}
