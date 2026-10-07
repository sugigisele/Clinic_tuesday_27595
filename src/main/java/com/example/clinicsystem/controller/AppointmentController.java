package com.example.clinicsystem.controller;

import java.util.List;
import java.util.Map;
import com.example.clinicsystem.model.domain.AppointmentStatus;
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
        return appointmentService.getAppointmentsBetweenDates(startDate, endDate);
    }
    @GetMapping("/status/by-status")
    public List<Object[]> Status () {
        return appointmentService.statsByStatus();
    }
    @PatchMapping("/cancel-day")
    public String cancelDay(@RequestParam Long doctorId, @RequestParam String date) {
        return appointmentService.cancelDay(doctorId, date);
    }
    @GetMapping("/page")
    public Page<Appointment> page(@RequestParam (defaultValue = "0") int page, @RequestParam (defaultValue = "5") int size, @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        String.Direction dir = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return appointmentService.page(page, size, Sort.by(dir, sortParams[0]));
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
        return appointmentService.deleteCancelledBefore(date);
    }
}