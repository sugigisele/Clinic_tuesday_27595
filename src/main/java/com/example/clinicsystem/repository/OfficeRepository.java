package com.example.clinicsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinicsystem.model.domain.Office;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    @Query("SELECT o FROM Office o WHERE o.roomNumber = :roomNumber")
    Optional<Office> findByRoomNumber(@Param("roomNumber") String roomNumber);
}