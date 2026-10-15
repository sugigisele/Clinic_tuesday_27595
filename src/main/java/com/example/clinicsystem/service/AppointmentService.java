package com.example.clinicsystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public String saveAppointment(Appointment appointment, Long patientId, Long doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (patient.isEmpty() || doctor.isEmpty()) {
            return "That patient or doctor is not available";
        }
        LocalDate day = appointment.getAppointmentDate();
        if (day == null) {
            return "Appointment date is required";
        }
        boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, day, AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }
        appointment.setDoctor(doctor.get());
        appointment.setPatient(patient.get());
        appointmentRepository.save(appointment);
        return "Appointment is saved successfully";
    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));
    }

    public List<Appointment> byStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return byStatus(status);
    }

    public List<Appointment> between(LocalDate start, LocalDate end) {
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Appointment> betweenDates(LocalDate start, LocalDate end) {
        return between(start, end);
    }

    public List<Object[]> statsByStatus() {
        return appointmentRepository.countByStatus();
    }

    public Page<Appointment> page(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    public Page<Appointment> getPage(Pageable pageable) {
        return page(pageable);
    }

    @Transactional
    public String cancelDay(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        int count = 0;
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() != AppointmentStatus.COMPLETED
                    && appointment.getStatus() != AppointmentStatus.CANCELLED) {
                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointmentRepository.save(appointment);
                count++;
            }
        }
        return count + " appointments cancelled";
    }

    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        List<Appointment> old = appointmentRepository
                .findByStatusAndAppointmentDateBefore(AppointmentStatus.CANCELLED, date);
        appointmentRepository.deleteAll(old);
        return old.size() + " appointments deleted";
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
}