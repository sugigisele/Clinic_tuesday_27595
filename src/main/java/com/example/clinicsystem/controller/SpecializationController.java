package com.example.clinicsystem.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.clinicsystem.model.domain.Doctor;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.service.SpecializationService;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService specializationService;

    @PostMapping("/save")
    public Specialization save(@RequestBody Specialization specialization) {
        return specializationService.save(specialization);
    }

    @GetMapping("/all")
    public List<Specialization> getAll() {
        return specializationService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Specialization> getById(@PathVariable Long id) {
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

    @PutMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestBody Specialization specialization) {
        return specializationService.update(id, specialization);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        return specializationService.delete(id);
    }
}