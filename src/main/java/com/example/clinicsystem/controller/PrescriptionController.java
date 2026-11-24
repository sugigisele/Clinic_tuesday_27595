package com.example.clinicsystem.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.clinicsystem.model.domain.Prescription;
import com.example.clinicsystem.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // C1: POST /api/prescriptions/save?appointmentId=1
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> save(@RequestParam Long appointmentId,
            @RequestBody Prescription prescription) {
        prescriptionService.save(appointmentId, prescription);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Prescription saved successfully"));
    }

    // C2
    @GetMapping("/appointment/{appointmentId}")
    public List<Prescription> getByAppointment(@PathVariable Long appointmentId) {
        return prescriptionService.byAppointment(appointmentId);
    }

    // C3 (the patient is identified by id, there is no ssNumber in this project)
    @GetMapping("/patient/{patientId}")
    public List<Prescription> getByPatient(@PathVariable Long patientId) {
        return prescriptionService.byPatient(patientId);
    }

    // C4
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable UUID id,
            @RequestBody Prescription prescription) {
        prescriptionService.update(id, prescription);
        return ResponseEntity.ok(Map.of("message", "Prescription updated successfully"));
    }

    // C4
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        prescriptionService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Prescription deleted successfully"));
    }
}
