package com.example.clinicsystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.model.domain.Patient;
import com.example.clinicsystem.repository.AppointmentRepository;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientService(PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
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
            throw new ResourceNotFoundException("The doctor with id does not exist");
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

    public List<Patient> getFrequentPatients(long min) {
        return patientRepository.findFrequentPatients(min);
    }

    public boolean doctorExists(Long doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    public List<Patient> getByDoctorId(Long doctorId) {
        return patientRepository.findPatientsByDoctorId(doctorId);
    }

    // D2: upcoming appointments (today or later, SCHEDULED or CONFIRMED), soonest
    // first
    public List<Appointment> upcoming(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("That patient with that ssnumber does not exist");
        }
        List<AppointmentStatus> open = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);
        return appointmentRepository
                .findByPatientIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        patientId, LocalDate.now(), open);
    }
}
