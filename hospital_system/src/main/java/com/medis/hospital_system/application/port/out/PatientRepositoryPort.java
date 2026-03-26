package com.medis.hospital_system.application.port.out;

import com.medis.hospital_system.domain.model.Patient;

import java.util.Optional;

public interface PatientRepositoryPort {

    Patient save(Patient patient);

    Optional<Patient> findById(Long id);

    Optional<Patient> findByUserId(Long userId);
}