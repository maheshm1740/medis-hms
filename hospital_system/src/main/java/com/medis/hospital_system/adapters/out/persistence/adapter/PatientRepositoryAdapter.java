package com.medis.hospital_system.adapters.out.persistence.adapter;

import com.medis.hospital_system.adapters.out.persistence.mapper.PatientMapper;
import com.medis.hospital_system.adapters.out.persistence.repository.PatientJpaRepository;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.domain.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository patientJpaRepository;

    public PatientRepositoryAdapter(PatientJpaRepository patientJpaRepository) {
        this.patientJpaRepository = patientJpaRepository;
    }

    @Override
    public Patient save(Patient patient) {
        return PatientMapper.toDomain(
                patientJpaRepository.save(PatientMapper.toEntity(patient))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Long id) {
        return patientJpaRepository.findById(id)
                .map(PatientMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByUserId(Long userId) {
        return patientJpaRepository.findByUserId(userId)
                .map(PatientMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientJpaRepository.findAll()
                .stream()
                .map(PatientMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Patient> getAllPatients(String name, Pageable pageable) {

        if (name == null || name.isBlank()) {
            return patientJpaRepository.findAll(pageable)
                    .map(PatientMapper::toDomain);
        }

        return patientJpaRepository.findByName(name, pageable)
                .map(PatientMapper::toDomain);
    }
}