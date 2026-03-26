package com.medis.hospital_system.domain.repository;

import com.medis.hospital_system.domain.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Patient save(Patient patient);

    Optional<Patient> findById(Long id);

    Optional<Patient> findByUserId(Long userId);

    List<Patient> findAll();

    Page<Patient> getAllPatients(String name, Pageable pageable);
}