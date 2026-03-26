// ============================================================
// FILE: DoctorServiceCacheTest.java
// src/test/java/com/medis/hospital_system/infrastructure/cache/DoctorServiceCacheTest.java
// ============================================================
package com.medis.hospital_system.infrastructure.cache;

import com.medis.hospital_system.application.service.DoctorService;
import com.medis.hospital_system.domain.model.Doctor;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import com.medis.hospital_system.domain.repository.UserRepository;
import com.medis.hospital_system.infrastructure.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class DoctorServiceCacheTest {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @MockitoBean
    private UserRepository userRepository;

    private Doctor sampleDoctor() {
        return new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    private User sampleUser() {
        return new User(1L, "Dr. John", "john@example.com", "password123", "9999999999", Role.DOCTOR, false);
    }

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    @Test
    void getDoctorById_shouldCacheOnFirstCall() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor()));

        doctorService.getDoctorById(1L);
        doctorService.getDoctorById(1L);
        doctorService.getDoctorById(1L);

        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void getDoctorByUserId_shouldCacheOnFirstCall() {
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(sampleDoctor()));

        doctorService.getDoctorByUserId(1L);
        doctorService.getDoctorByUserId(1L);

        verify(doctorRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getAllDoctors_shouldCacheOnFirstCall() {
        when(doctorRepository.findAll()).thenReturn(List.of(sampleDoctor()));

        doctorService.getAllDoctors();
        doctorService.getAllDoctors();

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void getDoctorsBySpecialization_shouldCacheOnFirstCall() {
        when(doctorRepository.findBySpecialization("Cardiology")).thenReturn(List.of(sampleDoctor()));

        doctorService.getDoctorsBySpecialization("Cardiology");
        doctorService.getDoctorsBySpecialization("Cardiology");

        verify(doctorRepository, times(1)).findBySpecialization("Cardiology");
    }

    @Test
    void updateDepartment_shouldEvictListCachesAndRepopulateSingleEntry() {
        Doctor doctor = sampleDoctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        doctorService.getAllDoctors();
        verify(doctorRepository, times(1)).findAll();

        doctorService.updateDepartment(1L, "Neurology");

        doctorService.getAllDoctors();
        verify(doctorRepository, times(2)).findAll();

        doctorService.getDoctorById(1L);
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void updateSpecialization_shouldEvictListAndSpecializationCaches() {
        Doctor doctor = sampleDoctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(doctorRepository.findBySpecialization("Cardiology")).thenReturn(List.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        doctorService.getAllDoctors();
        doctorService.getDoctorsBySpecialization("Cardiology");

        doctorService.updateSpecialization(1L, List.of("Neurology", "Psychiatry"));

        doctorService.getAllDoctors();
        doctorService.getDoctorsBySpecialization("Cardiology");

        verify(doctorRepository, times(2)).findAll();
        verify(doctorRepository, times(2)).findBySpecialization("Cardiology");
    }

    @Test
    void getDoctorById_cacheShouldContainEntryAfterFirstCall() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor()));

        doctorService.getDoctorById(1L);

        var cache = cacheManager.getCache(CacheNames.DOCTORS);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();
    }

    @Test
    void getAllDoctors_cacheShouldContainEntryAfterFirstCall() {
        when(doctorRepository.findAll()).thenReturn(List.of(sampleDoctor()));

        doctorService.getAllDoctors();

        var cache = cacheManager.getCache(CacheNames.DOCTORS_ALL);
        assertThat(cache).isNotNull();
        assertThat(cache.get("all")).isNotNull();
    }
}
