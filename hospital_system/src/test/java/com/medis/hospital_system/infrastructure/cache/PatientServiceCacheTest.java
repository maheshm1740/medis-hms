package com.medis.hospital_system.infrastructure.cache;

import com.medis.hospital_system.application.service.PatientService;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.PatientRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
class PatientServiceCacheTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private UserRepository userRepository;

    private Patient samplePatient() {
        return new Patient(1L, 2L, "O+", "123 MG Road, Bangalore", "9988776655");
    }

    private User validUser() {
        return new User(2L, "Jane Doe", "jane@example.com", "password123", "8888888888", Role.PATIENT, true);
    }

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    @Test
    void getPatientById_shouldCacheOnFirstCall() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient()));

        patientService.getPatientById(1L);
        patientService.getPatientById(1L);
        patientService.getPatientById(1L);

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientByUserId_shouldCacheOnFirstCall() {
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.of(samplePatient()));

        patientService.getPatientByUserId(2L);
        patientService.getPatientByUserId(2L);

        verify(patientRepository, times(1)).findByUserId(2L);
    }

    @Test
    void updateAddress_shouldEvictAndRepopulateCache() {
        Patient patient = samplePatient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.getPatientById(1L);
        verify(patientRepository, times(1)).findById(1L);

        patientService.updateAddress(1L, "456 Brigade Road, Bangalore");

        patientService.getPatientById(1L);

        // 1 for prime + 1 inside updateAddress (self-call bypasses proxy)
        verify(patientRepository, times(2)).findById(1L);
    }

    @Test
    void updateEmergencyContact_shouldEvictAndRepopulateCache() {
        Patient patient = samplePatient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.getPatientById(1L);
        patientService.updateEmergencyContact(1L, "1122334455");
        patientService.getPatientById(1L);

        // 1 for prime + 1 inside updateEmergencyContact
        verify(patientRepository, times(2)).findById(1L);
    }

    @Test
    void getPatientByUserId_shouldBeEvictedAfterUpdateAddress() {
        Patient patient = samplePatient();
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.of(patient));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.getPatientByUserId(2L);
        verify(patientRepository, times(1)).findByUserId(2L);

        patientService.updateAddress(1L, "New Address");

        patientService.getPatientByUserId(2L);
        verify(patientRepository, times(2)).findByUserId(2L);
    }

    @Test
    void getPatientById_cacheShouldContainEntryAfterFirstCall() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient()));

        patientService.getPatientById(1L);

        var cache = cacheManager.getCache(CacheNames.PATIENTS);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();
    }

    @Test
    void getPatientByUserId_cacheShouldContainEntryAfterFirstCall() {
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.of(samplePatient()));

        patientService.getPatientByUserId(2L);

        var cache = cacheManager.getCache(CacheNames.PATIENTS);
        assertThat(cache).isNotNull();
        assertThat(cache.get("user:2")).isNotNull();
    }
}
