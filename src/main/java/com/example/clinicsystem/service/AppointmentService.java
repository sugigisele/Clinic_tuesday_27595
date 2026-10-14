package com.example.clinicsystem.service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Patient;
import com.example.clinicsystem.repository.AppointmentRepository;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // CREATE
    @Transactional
public String saveAppointment(Appointment appointment, Long patientId, Long doctorId) {
    Optional<Doctor> doctor = doctorRepository.findById(doctorId);
    Optional<Patient> patient = patientRepository.findById(patientId);
    if (patient.isEmpty() || doctor.isEmpty()) {
        return "That patient or doctor is not available";
    }
    LocalDate day = appointment.getAppointmentDate();
    boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateBetweenAndStatusNot(
            doctorId, day.atStartOfDay(), day.atTime(LocalTime.MAX), AppointmentStatus.CANCELLED); // A4
    if (booked) return "Doctor is already booked on that date";

    appointment.setDoctor(doctor.get());
    appointment.setPatient(patient.get());
    appointmentRepository.save(appointment);
    return "Appointment is saved successfully";
}

public List<Appointment> byStatus(AppointmentStatus s) {
    return appointmentRepository.findByStatusOrderByAppointmentDateAsc(s);
}

public List<Appointment> between(LocalDate start, LocalDate end) {
    return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            start.atStartOfDay(), end.atTime(LocalTime.MAX));
}

public List<Object[]> statsByStatus() { return appointmentRepository.countByStatus(); }

@Transactional
public String cancelDay(Long doctorId, LocalDate date) {
    int n = appointmentRepository.cancelDay(doctorId, date.atStartOfDay(), date.atTime(LocalTime.MAX),
            AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
    return n + " appointments cancelled";
}

public Page<Appointment> page(Pageable pageable) { return appointmentRepository.findAll(pageable); } // bonus

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int count = appointmentRepository.deleteCancelledBefore(date, AppointmentStatus.CANCELLED);
        return count + " appointments deleted";
    }

    
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));
    }

    
    @Transactional
    public Appointment update(Long id, Appointment updated, Long patientId, Long doctorId) {
        Appointment existing = getById(id);

        if (updated.getAppointmentDate() != null) {
            existing.setAppointmentDate(updated.getAppointmentDate());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        if (patientId != null) {
            existing.setPatient(findPatient(patientId));
        }
        if (doctorId != null) {
            existing.setDoctor(findDoctor(doctorId));
        }
        return appointmentRepository.save(existing);
    }

    
    public void delete(Long id) {
        appointmentRepository.delete(getById(id));
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + id));
    }

         public List<Appointment> betweenDates(LocalDate start, LocalDate end) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

        public Page<Appointment> getPage(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }
}