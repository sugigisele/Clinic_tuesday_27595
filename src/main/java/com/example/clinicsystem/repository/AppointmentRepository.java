package com.example.clinicsystem.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate date, AppointmentStatus status);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    List<Appointment> findByStatusAndAppointmentDateBefore(AppointmentStatus status, LocalDate date);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();

    // D1: a doctor's appointments on one day, without the CANCELLED ones
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate date,
            AppointmentStatus status);

    // D2: a patient's appointments from a date onward, only some statuses, soonest
    // first
    List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            Long patientId, LocalDate date, List<AppointmentStatus> statuses);

    // D4: counts for the dashboard
    long countByAppointmentDate(LocalDate date);

    long countByStatusEquals(AppointmentStatus status);
}
