package com.example.clinicsystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.clinicsystem.exception.ConflictException;
import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Patient;
import com.example.clinicsystem.repository.AppointmentRepository;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.PatientRepository;
import com.example.clinicsystem.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // B1: safe booking
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
        if (day.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Appointment date cannot be in the past");
        }
        boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, day, AppointmentStatus.CANCELLED);
        if (booked) {
            return "Doctor is already booked on that date";
        }
        appointment.setId(null);
        appointment.setDoctor(doctor.get());
        appointment.setPatient(patient.get());
        appointment.setStatus(AppointmentStatus.SCHEDULED); // always SCHEDULED, ignore the JSON status
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

    // The general update NO LONGER changes the status (only the status endpoint
    // can)
    @Transactional
    public Appointment update(Long id, Appointment updated, Long patientId, Long doctorId) {
        Appointment existing = getById(id);
        if (updated.getAppointmentDate() != null) {
            existing.setAppointmentDate(updated.getAppointmentDate());
        }
        if (patientId != null) {
            existing.setPatient(findPatient(patientId));
        }
        if (doctorId != null) {
            existing.setDoctor(findDoctor(doctorId));
        }
        return appointmentRepository.save(existing);
    }

    // C4: an appointment with prescriptions cannot be deleted
    public void delete(Long id) {
        Appointment appointment = getById(id);
        if (prescriptionRepository.existsByAppointmentId(id)) {
            throw new ConflictException("That appointment has prescriptions, delete them first");
        }
        appointmentRepository.delete(appointment);
    }

    // B2: status flow
    // SCHEDULED -> CONFIRMED or CANCELLED
    // CONFIRMED -> COMPLETED or CANCELLED
    // COMPLETED and CANCELLED are final
    @Transactional
    public void changeStatus(Long id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The appointment with that id does not exist"));

        AppointmentStatus current = appointment.getStatus();
        boolean allowed = false;

        if (current == AppointmentStatus.SCHEDULED) {
            allowed = newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED;
        } else if (current == AppointmentStatus.CONFIRMED) {
            allowed = newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED;
        }

        if (!allowed) {
            throw new IllegalStateException("Cannot change status from " + current + " to " + newStatus);
        }
        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
    }

    // B3: reschedule
    @Transactional
    public void reschedule(Long id, LocalDate newDate) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The appointment with that id does not exist"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("A COMPLETED appointment cannot be rescheduled");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("A CANCELLED appointment cannot be rescheduled");
        }
        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Appointment date cannot be in the past");
        }
        boolean booked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
        if (booked) {
            throw new ConflictException("Doctor is already booked on that date");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
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
