package com.medis.hospital_system.domain.repository;

import com.medis.hospital_system.domain.model.Medicine;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository {

    Medicine save(Medicine medicine);

    Optional<Medicine> findById(Long id);

    Optional<Medicine> findByName(String name); // added — needed to look up by name

    boolean existsByName(String name); // added — needed to prevent duplicates

    List<Medicine> findAll();

    List<Medicine> searchMedicines(@Param("search") String search);
}