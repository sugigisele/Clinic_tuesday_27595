package com.example.clinicsystem.repository;
import java.util.List;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.clinicsystem.model.domain.Appointment;
import org.springframework.stereotype.Repository;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> { 

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(java.time.LocalDateTime start, java.time.LocalDateTime end);
    boolean existsByDoctorIdAndAppointmentDateBetween(Long doctorId, java.time.LocalDateTime start, java.time.LocalDateTime end, AppointmentStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :cancelled WHERE a.id = :doctorId" +"AND a.appointmentDate BETWEEN :start AND :end AND a.status <> : completed")
    int canclelDay(@Param("doctorId") Long doctorId, @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end, @Param("cancelled") AppointmentStatus cancelled, @Param("completed") AppointmentStatus completed);

    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.status = :cancelled AND a.appointmentDate BETWEEN :start AND :end AND a.status <> :completed")
    int cancelDay(@Param("doctorId") Long doctorId, @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end, @Param("cancelled") AppointmentStatus cancelled, @Param("completed") AppointmentStatus completed);
}