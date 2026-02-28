package com.rev.app.repository;

import com.rev.app.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    java.util.Optional<Designation> findByDesignationName(String name);
}
