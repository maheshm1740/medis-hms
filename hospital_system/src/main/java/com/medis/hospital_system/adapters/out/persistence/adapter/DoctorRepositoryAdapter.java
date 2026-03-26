package com.medis.hospital_system.adapters.out.persistence.adapter;

import com.medis.hospital_system.adapters.out.persistence.mapper.DoctorMapper;
import com.medis.hospital_system.adapters.out.persistence.repository.DoctorJpaRepository;
import com.medis.hospital_system.domain.model.Doctor;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class DoctorRepositoryAdapter implements DoctorRepository {

    private final DoctorJpaRepository doctorJpaRepository;

    public DoctorRepositoryAdapter(DoctorJpaRepository doctorJpaRepository) {
        this.doctorJpaRepository = doctorJpaRepository;
    }

    @Override
    public Doctor save(Doctor doctor) {
        return DoctorMapper.toDomain(
                doctorJpaRepository.save(DoctorMapper.toEntity(doctor))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Doctor> findById(Long id) {
        return doctorJpaRepository.findById(id)
                .map(DoctorMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Doctor> findByUserId(Long userId) {
        return doctorJpaRepository.findByUserId(userId)
                .map(DoctorMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> findAll() {
        return doctorJpaRepository.findAll()
                .stream()
                .map(DoctorMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorJpaRepository.findBySpecializationIgnoreCase(specialization)
                .stream()
                .map(DoctorMapper::toDomain)
                .toList();
    }
}