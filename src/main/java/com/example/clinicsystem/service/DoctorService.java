package com.example.clinicsystem.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         SpecializationRepository specializationRepository) {
        this.doctorRepository = doctorRepository;
        this.specializationRepository = specializationRepository;
    }

    @Transactional
    public Doctor create(Doctor doctor) {
        if (doctor.getOffice() == null) {
            throw new IllegalStateException("A doctor must have an office.");
        }
        doctor.setId(null);
        doctor.getOffice().setId(null);

        // replace the {"id":1} stubs from the JSON with the real specializations from the database
        Set<Specialization> resolved = new HashSet<>();
        if (doctor.getSpecializations() != null) {
            for (Specialization s : doctor.getSpecializations()) {
                if (s.getId() == null) {
                    throw new IllegalStateException("Each specialization needs an id.");
                }
                resolved.add(findSpecialization(s.getId()));
            }
        }
        doctor.setSpecializations(resolved);

        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public List <Doctor> bySpecialization(String name) {
        return doctorRepository.findBySpecializationName(name);
    }
    public List <Doctor> withoutOffice() {
        return doctorRepository.findWithoutOffice();
    }

    public Doctor getById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + id));
    }

    public Doctor update(Long id, Doctor updated) {
        Doctor existing = getById(id);
        existing.setFullName(updated.getFullName());
        if (updated.getOffice() != null) {
            existing.getOffice().setRoomNumber(updated.getOffice().getRoomNumber());
        }
        return doctorRepository.save(existing);
    }

    public void delete(Long id) {
        doctorRepository.delete(getById(id)); // office is deleted too; join-table rows are removed automatically
    }

    // ---------- Many-to-Many ----------

    @Transactional
    public Doctor addSpecialization(Long doctorId, Long specializationId) {
        Doctor doctor = getById(doctorId);
        Specialization specialization = findSpecialization(specializationId);
        if (doctor.getSpecializations().contains(specialization)) {
            throw new IllegalStateException("Doctor already has this specialization.");
        }
        doctor.getSpecializations().add(specialization);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor removeSpecialization(Long doctorId, Long specializationId) {
        Doctor doctor = getById(doctorId);
        Specialization specialization = findSpecialization(specializationId);
        if (!doctor.getSpecializations().remove(specialization)) {
            throw new IllegalStateException("Doctor does not have this specialization.");
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Set<Specialization> getSpecializations(Long doctorId) {
        return new HashSet<>(getById(doctorId).getSpecializations());
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with id " + id));
    }
}