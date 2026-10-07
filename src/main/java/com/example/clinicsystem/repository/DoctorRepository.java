package com.example.clinicsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinicsystem.model.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByFullName(String fullName);

    Boolean existsByFullName(String fullName);

    Boolean existsByOffice_RoomNumber(String roomNumber);

    Boolean existsByFullNameAndOffice_RoomNumber(String fullName, String roomNumber);

    @Query("SELECT d FROM Doctor d WHERE d.fullName = :fullName AND d.office.roomNumber = :roomNumber AND d.id <> :id")
    Optional<Doctor> findByFullNameAndOffice_RoomNumberAndIdNot(@Param("fullName") String fullName, @Param("roomNumber") String roomNumber, @Param("id") Long id);

    @Query("SELECT d FROM Doctor d WHERE d.fullName = :fullName AND d.office.roomNumber = :roomNumber")
    Optional<Doctor> findByFullNameAndOffice_RoomNumber(@Param("fullName") String fullName, @Param("roomNumber") String roomNumber);
}