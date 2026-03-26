package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.RegisterDoctorCommand;
import com.medis.hospital_system.application.dto.DoctorDetails;
import com.medis.hospital_system.application.port.in.DoctorUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Doctor;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import com.medis.hospital_system.domain.repository.UserRepository;
import com.medis.hospital_system.infrastructure.cache.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService implements DoctorUseCase {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    // ← toDetails() was accidentally deleted, add it back
    private DoctorDetails toDetails(Doctor doctor) {
        var user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", doctor.getUserId()));
        return new DoctorDetails(
                doctor.getId(),
                doctor.getUserId(),
                user.getName(),
                user.getEmail(),
                doctor.getSpecialization(),
                doctor.getDepartment(),
                doctor.getLicenseNumber(),
                doctor.getExperienceYears(),
                doctor.isExperiencedDoctor()
        );
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheNames.DOCTORS_ALL,     allEntries = true),
            @CacheEvict(value = CacheNames.DOCTORS_BY_SPEC, allEntries = true)
    })
    public DoctorDetails registerDoctor(RegisterDoctorCommand command) {
        // Resolve email to user
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> ResourceNotFoundException.of("User", command.email()));

        doctorRepository.findByUserId(user.getId()).ifPresent(existing -> {
            throw new DuplicateResourceException("A doctor profile already exists for: " + command.email());
        });

        Doctor doctor = new Doctor(
                null,
                user.getId(),      // ← resolved from email
                command.specialization(),
                command.department(),
                command.licenseNumber(),
                command.experienceYears()
        );
        return toDetails(doctorRepository.save(doctor));
    }

    @Override
    @Cacheable(value = CacheNames.DOCTORS, key = "#id")
    public DoctorDetails getDoctorById(Long id) {
        return toDetails(doctorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Doctor", id)));
    }

    @Override
    @Cacheable(value = CacheNames.DOCTORS, key = "'user:' + #userId")
    public DoctorDetails getDoctorByUserId(Long userId) {
        return toDetails(doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user id: " + userId)));
    }

    @Override
    public List<DoctorDetails> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::toDetails)
                .toList();
    }

    @Override
    public List<DoctorDetails> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(this::toDetails)
                .toList();
    }

    @Override
    @Caching(
            put   = { @CachePut(value = CacheNames.DOCTORS, key = "#doctorId") },
            evict = {
                    @CacheEvict(value = CacheNames.DOCTORS_ALL,     allEntries = true),
                    @CacheEvict(value = CacheNames.DOCTORS_BY_SPEC, allEntries = true),
                    @CacheEvict(value = CacheNames.DOCTORS, key = "'user:' + #result.userId()")
            }
    )
    public DoctorDetails updateDepartment(Long doctorId, String newDepartment) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> ResourceNotFoundException.of("Doctor", doctorId));
        doctor.updateDepartment(newDepartment);
        return toDetails(doctorRepository.save(doctor));
    }

    @Override
    @Caching(
            put   = { @CachePut(value = CacheNames.DOCTORS, key = "#doctorId") },
            evict = {
                    @CacheEvict(value = CacheNames.DOCTORS_ALL,     allEntries = true),
                    @CacheEvict(value = CacheNames.DOCTORS_BY_SPEC, allEntries = true),
                    @CacheEvict(value = CacheNames.DOCTORS, key = "'user:' + #result.userId()")
            }
    )
    public DoctorDetails updateSpecialization(Long doctorId, List<String> newSpecialization) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> ResourceNotFoundException.of("Doctor", doctorId));
        doctor.updateSpecialization(newSpecialization);
        return toDetails(doctorRepository.save(doctor));
    }

    @Override
    public DoctorDetails getDoctorByUserIdDirect(Long userId) {
        return toDetails(doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile not found for user id: " + userId)));
    }
}