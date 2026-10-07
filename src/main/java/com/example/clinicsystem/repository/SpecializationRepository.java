package com.example.clinicsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinicsystem.model.domain.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    @Query("SELECT s FROM Specialization s WHERE s.name = :name")
    Optional<Specialization> findByName(@Param("name") String name);
    
}