package com.example.clinicsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import com.example.clinicsystem.model.domain.Office;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    @Query("SELECT o FROM Office o WHERE o.roomNumber = :roomNumber")
    Optional<Office> findByRoomNumber(@Param("roomNumber") String roomNumber);

    @Query("SELECT o.name, o.officeNumber, COUNT(a) FROM Appointment a JOIN a.doctor d JOIN d.office o " +
           "GROUP BY o.id, o.name, o.officeNumber ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffice(Pageable pageable);
    
}