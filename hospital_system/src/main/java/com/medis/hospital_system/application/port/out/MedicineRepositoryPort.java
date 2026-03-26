package com.medis.hospital_system.application.port.out;

import com.medis.hospital_system.domain.model.Medicine;

import java.util.List;
import java.util.Optional;

public interface MedicineRepositoryPort {

    Medicine save(Medicine medicine);

    Optional<Medicine> findById(Long id);

    Optional<Medicine> findByName(String name);

    boolean existsByName(String name);

    List<Medicine> findAll();
}