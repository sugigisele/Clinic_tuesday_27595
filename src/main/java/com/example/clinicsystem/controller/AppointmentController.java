package com.example.clinicsystem.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // B1: e.g. POST /api/appointments/save?patientId=1&doctorId=1
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Appointment appointment,
            @RequestParam Long patientId,
            @RequestParam Long doctorId) {
        String msg = appointmentService.saveAppointment(appointment, patientId, doctorId);
        if (msg.equals("Doctor is already booked on that date")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
        }
        return ResponseEntity.ok(msg);
    }

    @GetMapping
    public List<Appointment> getAll() {
        return appointmentService.getAll();
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        return appointmentService.getById(id);
    }

    @GetMapping("/by-status")
    public List<Appointment> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @GetMapping("/between-dates")
    public List<Appointment> getAppointmentsBetweenDates(@RequestParam String startDate,
            @RequestParam String endDate) {
        return appointmentService.betweenDates(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping("/status/by-status")
    public List<Object[]> Status() {
        return appointmentService.statsByStatus();
    }

    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam Long doctorId, @RequestParam String date) {
        return appointmentService.cancelDay(doctorId, LocalDate.parse(date));
    }

    @GetMapping("/page")
    public Page<Appointment> getPage(@RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];

        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, field));
        return appointmentService.getPage(pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @RequestBody Appointment appointment,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId) {
        appointmentService.update(id, appointment, patientId, doctorId);
        return ResponseEntity.ok(Map.<String, Object>of("message", "Appointment updated successfully!"));
    }

    // B2: status flow
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> changeStatus(@PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        appointmentService.changeStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Appointment status updated successfully"));
    }

    // B3: reschedule
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<Map<String, String>> reschedule(@PathVariable Long id,
            @RequestParam String newDate) {
        appointmentService.reschedule(id, LocalDate.parse(newDate));
        return ResponseEntity.ok(Map.of("message", "Appointment rescheduled successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Appointment deleted successfully!"));
    }

    @DeleteMapping("/cancelled-before")
    public String deletOld(@RequestParam String date) {
        return appointmentService.deleteCancelledBefore(LocalDate.parse(date));
    }
}
