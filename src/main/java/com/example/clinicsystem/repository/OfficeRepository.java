package com.example.clinicsystem.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.clinicsystem.model.domain.Office;

public interface OfficeRepository extends JpaRepository<Office, Long> {

    @Query("SELECT o FROM Office o WHERE o.roomNumber = :roomNumber")
    Optional<Office> findByRoomNumber(@Param("roomNumber") String roomNumber);

    @Query("SELECT o.id, o.roomNumber, COUNT(a) FROM Appointment a JOIN a.doctor d JOIN d.office o GROUP BY o.id, o.roomNumber ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffice(Pageable pageable);

    // A3: offices that no doctor occupies, sorted by room number
    @Query("SELECT o FROM Office o WHERE NOT EXISTS (SELECT d FROM Doctor d WHERE d.office = o) ORDER BY o.roomNumber")
    List<Office> findFreeOffices();
}
