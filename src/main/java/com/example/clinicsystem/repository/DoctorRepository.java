package com.example.clinicsystem.repository;

import java.util.Optional;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
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

        
    @Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationName(@Param("name") String name);

    
    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName")
    List<Doctor> findWithoutOffice();

    
}