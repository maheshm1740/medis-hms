package com.medis.hospital_system.application.port.out;

import com.medis.hospital_system.domain.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepositoryPort {

    Doctor save(Doctor doctor);

    Optional<Doctor> findById(Long id);

    Optional<Doctor> findByUserId(Long userId);

    List<Doctor> findAll();

    List<Doctor> findBySpecialization(String specialization);
}