package com.example.clinicsystem.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clinicsystem.exception.ConflictException;
import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Office;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.repository.AppointmentRepository;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.OfficeRepository;
import com.example.clinicsystem.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final OfficeRepository officeRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         SpecializationRepository specializationRepository,
                         OfficeRepository officeRepository,
                         AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.specializationRepository = specializationRepository;
        this.officeRepository = officeRepository;
        this.appointmentRepository = appointmentRepository;
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

    public List<Doctor> bySpecialization(String name) {
        return doctorRepository.findBySpecializationName(name);
    }

    public List<Doctor> withoutOffice() {
        return doctorRepository.findByOfficeIsNullOrderByFullName();
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
        doctorRepository.delete(getById(id));
    }

    // ---------------- Part A ----------------

    // A1: add ONE specialization and keep the ones the doctor already has
    @Transactional
    public void addSpecialization(Long doctorId, Long specializationId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        Specialization specialization = findSpecialization(specializationId);

        if (doctor.getSpecializations().contains(specialization)) {
            throw new ConflictException("The doctor already has that specialization");
        }
        doctor.getSpecializations().add(specialization); // add to the list
        doctorRepository.save(doctor);                    // save the owner (Doctor)
    }

    // A2: remove only the link, the specialization itself stays
    @Transactional
    public void removeSpecialization(Long doctorId, Long specializationId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        Specialization specialization = findSpecialization(specializationId);

        if (!doctor.getSpecializations().contains(specialization)) {
            throw new ConflictException("The doctor does not have that specialization");
        }
        doctor.getSpecializations().remove(specialization); // remove from the list
        doctorRepository.save(doctor);
    }

    @Transactional
    public Set<Specialization> getSpecializations(Long doctorId) {
        return new HashSet<>(getById(doctorId).getSpecializations());
    }

    // A4: move a doctor to another office
    @Transactional
    public void changeOffice(Long doctorId, String officeNumber) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        Office office = officeRepository.findByRoomNumber(officeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("The office with that office number does not exist"));

        // the office is taken if ANOTHER doctor already uses it
        if (doctorRepository.existsByOffice_RoomNumberAndIdNot(officeNumber, doctorId)) {
            throw new ConflictException("That office is taken by other doctor");
        }
        doctor.setOffice(office); // the office column is in Doctor, so we change it here
        doctorRepository.save(doctor);
    }

    // ---------------- Part D ----------------

    // D1: agenda of a doctor for one day, without CANCELLED appointments
    public List<Appointment> agenda(Long doctorId, LocalDate date) {
        findDoctorOrThrow(doctorId); // 404 if the doctor does not exist
        return appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, date, AppointmentStatus.CANCELLED);
    }

    // D3: search doctors by name
    public List<Doctor> search(String name) {
        return doctorRepository.searchByName(name);
    }

    // ---------------- helpers ----------------

    private Doctor findDoctorOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The doctor with that id does not exist"));
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("That specialization does not exist"));
    }
}
