package com.example.clinicsystem.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.clinicsystem.model.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByFullName(String fullName);

    Boolean existsByFullName(String fullName);

    Boolean existsByOffice_RoomNumber(String roomNumber);

    Boolean existsByFullNameAndOffice_RoomNumber(String fullName, String roomNumber);

    Optional<Doctor> findByFullNameAndOffice_RoomNumberAndIdNot(String fullName, String roomNumber, Long id);

    Optional<Doctor> findByFullNameAndOffice_RoomNumber(String fullName, String roomNumber);

    @Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationName(@Param("name") String name);

    List<Doctor> findByOfficeIsNullOrderByFullName();

    // A4 (derived): is this office used by a doctor other than the given one?
    boolean existsByOffice_RoomNumberAndIdNot(String roomNumber, Long id);

    // D3 (JPQL): name contains the text, ignoring letter case
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.fullName")
    List<Doctor> searchByName(@Param("name") String name);
}
