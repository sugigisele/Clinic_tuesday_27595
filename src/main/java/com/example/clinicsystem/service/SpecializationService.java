package com.example.clinicsystem.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.repository.SpecializationRepository;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public Specialization create(Specialization specialization) {
        specialization.setId(null);
        return specializationRepository.save(specialization);
    }

    public List<Specialization> getAll() {
        return specializationRepository.findAll();
    }
    public List<Specialization> unsend() {
        return specializationRepository.findUnsed();
    }

    public Specialization getById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id " + id));
    }

    public Specialization update(Long id, Specialization updated) {
        Specialization existing = getById(id);
        existing.setName(updated.getName());
        return specializationRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Specialization existing = getById(id);
        // Doctor owns the relationship, so first remove this specialization from every doctor
        for (Doctor doctor : new HashSet<>(existing.getDoctors())) {
            doctor.getSpecializations().remove(existing);
        }
        specializationRepository.delete(existing);
    }

    @Transactional
    public List<Doctor> getDoctors(Long id) {
        return new ArrayList<>(getById(id).getDoctors());
    }
}