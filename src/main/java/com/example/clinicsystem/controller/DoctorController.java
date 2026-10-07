package com.example.clinicsystem.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.<String, Object>of("message", "Doctor created successfully!", "data", saved));
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



// ====================Many to Mant : Doctor and  specialization========================

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

    @PostMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<Map<String, Object>> addSpecialization(@PathVariable Long doctorId,
                                                                 @PathVariable Long specializationId) {
        Doctor saved = doctorService.addSpecialization(doctorId, specializationId);
        return ResponseEntity.ok(
                Map.<String, Object>of("message", "Specialization assigned to doctor successfully"));
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<Map<String, Object>> removeSpecialization(@PathVariable Long doctorId,
                                                                    @PathVariable Long specializationId) {
        Doctor saved = doctorService.removeSpecialization(doctorId, specializationId);
        return ResponseEntity.ok(
                Map.<String, Object>of("message", "Specialization removed from doctor successfully"));
    }
}
