package com.example.clinicsystem.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.clinicsystem.model.domain.Specialization;
import com.example.clinicsystem.repository.SpecializationRepository;
import com.example.clinicsystem.model.domain.Doctor;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepository;

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public List<Specialization> getAll() {
        return specializationRepository.findAll();
    }

    public Optional<Specialization> getById(Long id) {
        return specializationRepository.findById(id);
    }

    public String update(Long id, Specialization newSpec) {
        Optional<Specialization> found = specializationRepository.findById(id);
        if (found.isEmpty()) {
            return "Specialization with that id does not exist";
        }
        Specialization spec = found.get();
        spec.setName(newSpec.getName());
        specializationRepository.save(spec);
        return "Specialization updated successfully";
    }

    public String delete(Long id) {
        if (!specializationRepository.existsById(id)) {
            return "Specialization with that id does not exist";
        }
        specializationRepository.deleteById(id);
        return "Specialization deleted successfully";
    }

    public List<Specialization> getUnused() {
        return specializationRepository.findUnused();
    }
            public List<Doctor> getDoctors(Long id) {
        Optional<Specialization> found = specializationRepository.findById(id);
        if (found.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(found.get().getDoctors());
    }
}