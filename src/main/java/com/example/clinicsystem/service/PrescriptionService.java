package com.example.clinicsystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.model.domain.Prescription;
import com.example.clinicsystem.repository.AppointmentRepository;
import com.example.clinicsystem.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // C1: write a prescription (only for COMPLETED appointments)
    @Transactional
    public void save(Long appointmentId, Prescription prescription) {
        Appointment appointment = findAppointment(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Prescriptions are only for COMPLETED appointments");
        }
        prescription.setId(null); // always a new row
        prescription.setIssuedDate(LocalDate.now()); // the service sets today's date
        prescription.setAppointment(appointment); // link to the appointment
        prescriptionRepository.save(prescription);
    }

    // C2: prescriptions of an appointment
    public List<Prescription> byAppointment(Long appointmentId) {
        Appointment appointment = findAppointment(appointmentId); // 404 if missing
        return prescriptionRepository.findByAppointment(appointment);
    }

    // C3: prescription history of a patient, newest first
    public List<Prescription> byPatient(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    // C4: update only medicineName, dosage and durationDays
    @Transactional
    public void update(UUID id, Prescription updated) {
        Prescription existing = findPrescription(id);
        existing.setMedicineName(updated.getMedicineName());
        existing.setDosage(updated.getDosage());
        existing.setDurationDays(updated.getDurationDays());
        prescriptionRepository.save(existing);
    }

    // C4: delete
    @Transactional
    public void delete(UUID id) {
        prescriptionRepository.delete(findPrescription(id));
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The appointment with that id does not exist"));
    }

    private Prescription findPrescription(UUID id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The prescription with that id does not exist"));
    }
}
