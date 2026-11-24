package com.example.clinicsystem.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    // C2 (derived): all prescriptions of one appointment
    List<Prescription> findByAppointment(Appointment appointment);

    // C4 (derived): does this appointment have at least one prescription?
    boolean existsByAppointmentId(Long appointmentId);

    // C3 (JPQL): prescription -> appointment -> patient -> id, newest first
    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.id = :patientId ORDER BY p.issuedDate DESC")
    List<Prescription> findByPatientId(@Param("patientId") Long patientId);
}
