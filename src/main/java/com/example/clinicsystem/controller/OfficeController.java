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

import com.example.clinicsystem.model.domain.Office;
import com.example.clinicsystem.service.OfficeService;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Office office) {
        Office saved = officeService.create(office);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("message", "Office created successfully!", "data", saved));
    }

    @GetMapping
    public List<Office> getAll() {
        return officeService.getAll();
    }

    // A3: offices without a doctor
    @GetMapping("/free")
    public List<Office> getFreeOffices() {
        return officeService.freeOffices();
    }

    @GetMapping("/{id}")
    public Office getById(@PathVariable Long id) {
        return officeService.getById(id);
    }

    @GetMapping("/busiest")
    public Object getBusiestOffice() {
        return officeService.busiest();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Office office) {
        officeService.update(id, office);
        return ResponseEntity.ok(Map.<String, Object>of("message", "Office updated successfully!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        officeService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Office deleted successfully!"));
    }
}
