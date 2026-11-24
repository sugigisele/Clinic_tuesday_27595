package com.example.clinicsystem.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.example.clinicsystem.model.domain.AppointmentStatus;
import com.example.clinicsystem.repository.AppointmentRepository;
import com.example.clinicsystem.repository.DoctorRepository;
import com.example.clinicsystem.repository.OfficeRepository;
import com.example.clinicsystem.repository.PatientRepository;
import com.example.clinicsystem.repository.PrescriptionRepository;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DashboardService(PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            OfficeRepository officeRepository,
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // D4: LinkedHashMap keeps the order in which we put the values
    public Map<String, Long> summary() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("patients", patientRepository.count());
        result.put("doctors", doctorRepository.count());
        result.put("freeOffices", (long) officeRepository.findFreeOffices().size()); // reuse A3
        result.put("appointmentsToday", appointmentRepository.countByAppointmentDate(LocalDate.now()));
        result.put("scheduled", appointmentRepository.countByStatusEquals(AppointmentStatus.SCHEDULED));
        result.put("prescriptions", prescriptionRepository.count());
        return result;
    }
}
