package com.example.clinicsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinicsystem.model.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByFullNameContainingIgnoreCase(String fullName);

    @Query("SELECT p FROM Patient p WHERE p.fullName = :fullName AND p.dateOfBirth = :dateOfBirth")
    Optional<Patient> findByFullNameAndDateOfBirth(@Param("fullName") String fullName, @Param("dateOfBirth") LocalDate dateOfBirth);

    @Query("SELECT p FROM Patient p WHERE p.fullName = :fullName AND p.dateOfBirth = :dateOfBirth AND p.id <> :id")
    Optional<Patient> findByFullNameAndDateOfBirthAndIdNot(@Param("fullName") String fullName, @Param("dateOfBirth") LocalDate dateOfBirth, @Param("id") Long id);
}