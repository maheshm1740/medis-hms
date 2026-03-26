package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.RegisterDoctorCommand;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Doctor;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import com.medis.hospital_system.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DoctorService doctorService;

    private User validUser() {
        return new User(2L, "Jane Doe", "jane@example.com", "password123", "8888888888", Role.PATIENT, true);
    }

    private Doctor validDoctor() {
        return new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    private RegisterDoctorCommand registerCommand() {
        return new RegisterDoctorCommand(1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    @Test
    void shouldRegisterDoctorSuccessfully() {
        RegisterDoctorCommand command = registerCommand();

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser()));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(validDoctor());

        Doctor result = doctorService.registerDoctor(command);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getLicenseNumber()).isEqualTo("KA-MED-001");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundDuringDoctorRegistration() {
        RegisterDoctorCommand command = registerCommand();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.registerDoctor(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDoctorProfileAlreadyExists() {
        RegisterDoctorCommand command = registerCommand();

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser()));
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(validDoctor()));

        assertThatThrownBy(() -> doctorService.registerDoctor(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("doctor profile");

        verify(doctorRepository, never()).save(any());
    }

    @Test
    void shouldGetDoctorByIdSuccessfully() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor()));

        Doctor result = doctorService.getDoctorById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenDoctorNotFoundById() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor");
    }

    @Test
    void shouldGetDoctorByUserIdSuccessfully() {
        when(doctorRepository.findByUserId(1L)).thenReturn(Optional.of(validDoctor()));

        Doctor result = doctorService.getDoctorByUserId(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenDoctorNotFoundByUserId() {
        when(doctorRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor");
    }

    @Test
    void shouldGetAllDoctorsSuccessfully() {
        when(doctorRepository.findAll()).thenReturn(List.of(validDoctor()));

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldGetDoctorsBySpecializationSuccessfully() {
        when(doctorRepository.findBySpecialization("Cardiology")).thenReturn(List.of(validDoctor()));

        List<Doctor> result = doctorService.getDoctorsBySpecialization("Cardiology");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialization()).contains("Cardiology");
    }

    @Test
    void shouldUpdateDepartmentSuccessfully() {
        Doctor doctor = validDoctor();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.updateDepartment(1L, "Neurology");

        assertThat(result.getDepartment()).isEqualTo("Neurology");
        verify(doctorRepository).save(doctor);
    }

    @Test
    void shouldUpdateSpecializationSuccessfully() {
        Doctor doctor = validDoctor();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.updateSpecialization(1L, List.of("Neurology", "Psychiatry"));

        assertThat(result.getSpecialization()).containsExactly("Neurology", "Psychiatry");
        verify(doctorRepository).save(doctor);
    }
}