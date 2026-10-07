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

import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Specialization specialization) {
        Specialization saved = specializationService.create(specialization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("message", "Specialization created successfully"));
    }

    @GetMapping
    public List<Specialization> getAll() {
        return specializationService.getAll();
    }

    @GetMapping("/{id}")
    public Specialization getById(@PathVariable Long id) {
        return specializationService.getById(id);
    }

    @GetMapping("/{id}/doctors")
    public List<Doctor> getDoctors(@PathVariable Long id) {
        return specializationService.getDoctors(id);
    }
    @GetMapping("/unused")
    public List<Specialization> getUnused() {
        return specializationService.getUnused();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                      @RequestBody Specialization specialization) {
        Specialization saved = specializationService.update(id, specialization);
        return ResponseEntity.ok(
                Map.<String, Object>of("message", "Specialization updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        specializationService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Specialization with id " + id + " deleted successfully"));
    }
}