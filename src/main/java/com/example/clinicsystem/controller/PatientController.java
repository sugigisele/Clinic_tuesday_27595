package com.example.clinicsystem.controller;

import java.util.List;
import java.util.Map;

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

import com.example.clinicsystem.model.domain.Patient;
import com.example.clinicsystem.service.PatientService;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Patient patient) {
        Patient saved = patientService.create(patient);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("message", "Patient created successfully!", "data", saved));
    }

    @GetMapping
    public List<Patient> getAll() {
        return patientService.getAll();
    }

    @GetMapping("/{id}")
    public Patient getById(@PathVariable Long id) {
        return patientService.getById(id);
    }
    @GetMapping("/by-name")
    public List<Patient> getByName(@RequestParam String name) {
        return patientService.getByName(name);
    }
    @GetMapping("/of-doctor/{doctorId}")
    public List<Patient> getByDoctorId(@PathVariable Long doctorId) {
        return patientService.getByDoctorId(doctorId);
    }
    @GetMapping("/frequent")
    public List<Patient> getFrequentPatients(@RequestParam long min) {
        return patientService.getFrequentPatients();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Patient patient) {
        Patient saved = patientService.update(id, patient);
        return ResponseEntity.ok(Map.<String, Object>of("message", "Patient updated successfully!", "data", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Patient deleted successfully!"));
    }
}