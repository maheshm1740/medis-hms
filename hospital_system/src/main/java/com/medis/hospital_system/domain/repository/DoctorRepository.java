package com.medis.hospital_system.domain.repository;

import com.medis.hospital_system.domain.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {

    Doctor save(Doctor doctor);

    Optional<Doctor> findById(Long id);

    Optional<Doctor> findByUserId(Long userId); // added — needed to look up doctor by user account

    List<Doctor> findAll();

    List<Doctor> findBySpecialization(String specialization); // added — useful for filtering
}