// ============================================================
// FILE 1 of 6:
// src/test/java/com/medis/hospital_system/infrastructure/cache/AppointmentServiceCacheTest.java
// ============================================================
package com.medis.hospital_system.infrastructure.cache;

import com.medis.hospital_system.application.command.BookAppointmentCommand;
import com.medis.hospital_system.application.command.RescheduleAppointmentCommand;
import com.medis.hospital_system.application.service.AppointmentService;
import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import com.medis.hospital_system.domain.model.Doctor;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.domain.repository.AppointmentRepository;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import com.medis.hospital_system.domain.repository.PatientRepository;
import com.medis.hospital_system.infrastructure.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AppointmentServiceCacheTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private AppointmentRepository appointmentRepository;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @MockitoBean
    private PatientRepository patientRepository;

    private final LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

    private Appointment sampleAppointment() {
        return new Appointment(1L, 1L, 1L, futureTime, AppointmentStatus.SCHEDULED, "Checkup");
    }

    private Doctor sampleDoctor() {
        return new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    private Patient samplePatient() {
        return new Patient(1L, 2L, "O+", "123 MG Road", "9988776655");
    }

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    @Test
    void getAppointmentById_shouldCacheOnFirstCall() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(sampleAppointment()));

        appointmentService.getAppointmentById(1L);
        appointmentService.getAppointmentById(1L);
        appointmentService.getAppointmentById(1L);

        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void getAppointmentsByDoctor_shouldCacheOnFirstCall() {
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(List.of(sampleAppointment()));

        appointmentService.getAppointmentsByDoctor(1L);
        appointmentService.getAppointmentsByDoctor(1L);

        verify(appointmentRepository, times(1)).findByDoctorId(1L);
    }

    @Test
    void getAppointmentsByPatient_shouldCacheOnFirstCall() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(sampleAppointment()));

        appointmentService.getAppointmentsByPatient(1L);
        appointmentService.getAppointmentsByPatient(1L);

        verify(appointmentRepository, times(1)).findByPatientId(1L);
    }

    @Test
    void getAppointmentsByStatus_shouldCacheOnFirstCall() {
        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(sampleAppointment()));

        appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED);
        appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED);

        verify(appointmentRepository, times(1)).findByStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    void bookAppointment_shouldEvictRelatedCaches() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient()));
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(List.of(sampleAppointment()));
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(sampleAppointment()));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(any(), any(), any()))
                .thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(sampleAppointment());

        appointmentService.getAppointmentsByDoctor(1L);
        appointmentService.getAppointmentsByPatient(1L);
        verify(appointmentRepository, times(1)).findByDoctorId(1L);
        verify(appointmentRepository, times(1)).findByPatientId(1L);

        appointmentService.bookAppointment(
                new BookAppointmentCommand(1L, 1L, futureTime.plusHours(2), "Follow-up")
        );

        appointmentService.getAppointmentsByDoctor(1L);
        appointmentService.getAppointmentsByPatient(1L);
        verify(appointmentRepository, times(2)).findByDoctorId(1L);
        verify(appointmentRepository, times(2)).findByPatientId(1L);
    }

    @Test
    void completeAppointment_shouldEvictListCachesAndRepopulateSingleEntry() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(List.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.getAppointmentsByDoctor(1L);
        verify(appointmentRepository, times(1)).findByDoctorId(1L);

        appointmentService.completeAppointment(1L);

        appointmentService.getAppointmentsByDoctor(1L);
        verify(appointmentRepository, times(2)).findByDoctorId(1L);

        appointmentService.getAppointmentById(1L);
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void cancelAppointment_shouldEvictListCachesAndRepopulateSingleEntry() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.getAppointmentsByPatient(1L);
        appointmentService.cancelAppointment(1L);
        appointmentService.getAppointmentsByPatient(1L);

        verify(appointmentRepository, times(2)).findByPatientId(1L);
    }

    @Test
    void rescheduleAppointment_shouldEvictListCachesAndRepopulateSingleEntry() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(any(), any(), any()))
                .thenReturn(List.of());
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(List.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.getAppointmentsByDoctor(1L);
        appointmentService.rescheduleAppointment(
                new RescheduleAppointmentCommand(1L, futureTime.plusDays(2))
        );
        appointmentService.getAppointmentsByDoctor(1L);

        verify(appointmentRepository, times(2)).findByDoctorId(1L);
    }

    @Test
    void getAppointmentById_cacheShouldContainEntryAfterFirstCall() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(sampleAppointment()));

        appointmentService.getAppointmentById(1L);

        var cache = cacheManager.getCache(CacheNames.APPOINTMENTS);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();
    }
}
