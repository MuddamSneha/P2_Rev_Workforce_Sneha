package com.rev.app.service;

import com.rev.app.dto.AnnouncementDto;

import java.util.List;

public interface AnnouncementService {
    AnnouncementDto createAnnouncement(AnnouncementDto announcementDto);

    List<AnnouncementDto> getAllAnnouncements();

    void deleteAnnouncement(Long announcementId);

    AnnouncementDto updateAnnouncement(AnnouncementDto announcementDto);
}
