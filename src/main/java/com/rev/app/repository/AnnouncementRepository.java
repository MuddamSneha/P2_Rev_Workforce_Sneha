package com.rev.app.repository;

import com.rev.app.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    org.springframework.data.domain.Page<Announcement> findAllByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);
}



