package com.example.clinicsystem.repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinicsystem.model.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByFullNameContainingIgnoreCase(String fullName);

    @Query("SELECT p FROM Patient p WHERE p.fullName = :fullName AND p.dateOfBirth = :dateOfBirth")
    Optional<Patient> findByFullNameAndDateOfBirth(@Param("fullName") String fullName, @Param("dateOfBirth") LocalDate dateOfBirth);

    @Query("SELECT p FROM Patient p WHERE p.fullName = :fullName AND p.dateOfBirth = :dateOfBirth AND p.id <> :id")
    Optional<Patient> findByFullNameAndDateOfBirthAndIdNot(@Param("fullName") String fullName, @Param("dateOfBirth") LocalDate dateOfBirth, @Param("id") Long id);

       
    @Query("SELECT p FROM Appointment a JOIN a.patient p GROUP BY p HAVING COUNT(a) >= :min ORDER BY COUNT(a) DESC")
    List<Patient> findFrequentPatients(@Param("min") long min);

   
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Patient> findPatientsByDoctorId(@Param("doctorId") Long doctorId);

}