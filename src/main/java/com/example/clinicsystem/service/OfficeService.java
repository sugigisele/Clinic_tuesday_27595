package com.example.clinicsystem.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.clinicsystem.exception.ResourceNotFoundException;
import com.example.clinicsystem.model.domain.Office;
import com.example.clinicsystem.repository.OfficeRepository;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    public Office create(Office office) {
        office.setId(null);
        return officeRepository.save(office);
    }

    public List<Office> getAll() {
        return officeRepository.findAll();
    }

    public Object busiest() {
        List<Object[]> result = officeRepository.findBusiestOffice(PageRequest.of(0, 1));
        if (result.isEmpty()) {
            return "No appointments yet";
        }
        Object[] row = result.get(0);
        Map<String, Object> office = new LinkedHashMap<>();
        office.put("id", row[0]);
        office.put("roomNumber", row[1]);
        office.put("appointments", row[2]);
        return office;
    }

    public Office getById(Long id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office not found with id " + id));
    }

    public Office update(Long id, Office updated) {
        Office existing = getById(id);
        existing.setRoomNumber(updated.getRoomNumber());
        return officeRepository.save(existing);
    }

    public void delete(Long id) {
        Office existing = getById(id);
        if (existing.getDoctor() != null) {
            throw new IllegalStateException("Office is assigned to a doctor. Delete the doctor first.");
        }
        officeRepository.delete(existing);
    }
}