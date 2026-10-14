package com.example.clinicsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Patient;
import com.example.clinicsystem.repository.PatientRepository;
import com.example.clinicsystem.repository.DoctorRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient create(Patient patient) {
        patient.setId(null); // forces a new INSERT even if the client sent an id
        return patientRepository.save(patient);
    }

    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    public List<Patient> findByName(String name) {
        return patientRepository.findByFullNameContainingIgnoreCase(name);
    }
    public List<Patient> frequent(long min) {
        return patientRepository.findFrequentPatients(min);
    }

    public List<Patient> ofDoctor(long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("The doctor with id does not exist" );
        }
        return patientRepository.findPatientsByDoctorId(doctorId);
    }

    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
    }

    public Patient update(Long id, Patient updated) {
        Patient existing = getById(id); // 404 if it does not exist
        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setGender(updated.getGender());
        existing.setAddress(updated.getAddress());
        return patientRepository.save(existing); // existing has an id, so this is an UPDATE
    }

    public void delete(Long id) {
        patientRepository.delete(getById(id));
    }

    @Autowired
    private DoctorRepository doctorRepository;

        public List<Patient> getFrequentPatients(long min) {
        return patientRepository.findFrequentPatients(min);
    }

    public boolean doctorExists(Long doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    public List<Patient> getByDoctorId(Long doctorId) {
        return patientRepository.findPatientsByDoctorId(doctorId);
    }
}