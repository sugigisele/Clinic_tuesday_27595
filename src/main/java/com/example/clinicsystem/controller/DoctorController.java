package com.example.clinicsystem.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Doctor doctor) {
        Doctor saved = doctorService.create(doctor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("message", "Doctor created successfully!", "data", saved));
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorService.getAll();
    }

    @GetMapping("/{id}")
    public Doctor getById(@PathVariable Long id) {
        return doctorService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Doctor doctor) {
        Doctor saved = doctorService.update(id, doctor);
        return ResponseEntity.ok(Map.<String, Object>of("message", "Doctor updated successfully!", "data", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully!"));
    }

    @GetMapping("/{doctorId}/specializations")
    public Set<Specialization> getSpecializations(@PathVariable Long doctorId) {
        return doctorService.getSpecializations(doctorId);
    }

    @GetMapping("/by-specialization/{name}")
    public List<Doctor> getDoctorsBySpecialization(@PathVariable String name) {
        return doctorService.bySpecialization(name);
    }

    @GetMapping("/without-office")
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorService.withoutOffice();
    }

    // A1: add one specialization
    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<Map<String, String>> addSpecialization(@PathVariable Long doctorId,
            @PathVariable Long specializationId) {
        doctorService.addSpecialization(doctorId, specializationId);
        return ResponseEntity.ok(Map.of("message", "Specialization added to the doctor"));
    }

    // A2: remove one specialization
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<Map<String, String>> removeSpecialization(@PathVariable Long doctorId,
            @PathVariable Long specializationId) {
        doctorService.removeSpecialization(doctorId, specializationId);
        return ResponseEntity.ok(Map.of("message", "Specialization removed from the doctor"));
    }

    // A4: move a doctor to another office
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<Map<String, String>> changeOffice(@PathVariable Long doctorId,
            @RequestParam String officeNumber) {
        doctorService.changeOffice(doctorId, officeNumber);
        return ResponseEntity.ok(Map.of("message", "Office changed successfully"));
    }

    // D1: agenda of a doctor for one day
    @GetMapping("/{doctorId}/agenda")
    public List<Appointment> getAgenda(@PathVariable Long doctorId, @RequestParam String date) {
        return doctorService.agenda(doctorId, LocalDate.parse(date));
    }

    // D3: search doctors by name
    @GetMapping("/search")
    public List<Doctor> search(@RequestParam String name) {
        return doctorService.search(name);
    }
}
