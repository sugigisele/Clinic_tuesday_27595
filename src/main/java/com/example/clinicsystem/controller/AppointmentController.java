package com.example.clinicsystem.controller;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

import com.example.clinicsystem.model.domain.Appointment;
import com.example.clinicsystem.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

     @PostMapping("/save")   // A4, e.g. POST /save?patientId=1&doctorId=1
    public ResponseEntity<String> save(@RequestBody Appointment appointment,
                                       @RequestParam Long patientId, @RequestParam Long doctorId) {
        String msg = appointmentService.saveAppointment(appointment, patientId, doctorId);
        if (msg.equals("Doctor is already booked on that date"))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
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
    public List<Appointment> getAppointmentsBetweenDates(@RequestParam String startDate, @RequestParam String endDate) {
        return appointmentService.betweenDates(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }
    @GetMapping("/status/by-status")
    public List<Object[]> Status () {
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
        Appointment saved = appointmentService.update(id, appointment, patientId, doctorId);
        return ResponseEntity.ok(Map.<String, Object>of("message", "Appointment updated successfully!"));
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