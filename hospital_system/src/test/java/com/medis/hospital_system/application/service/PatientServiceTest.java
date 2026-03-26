package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.RegisterPatientCommand;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.PatientRepository;
import com.medis.hospital_system.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    private User validUser() {
        return new User(2L, "Jane Doe", "jane@example.com", "password123", "8888888888", Role.PATIENT, true);
    }

    private Patient validPatient() {
        return new Patient(1L, 2L, "O+", "123 MG Road, Bangalore", "9988776655");
    }

    private RegisterPatientCommand registerCommand() {
        return new RegisterPatientCommand(2L, "O+", "123 MG Road, Bangalore", "9988776655");
    }

    @Test
    void shouldRegisterPatientSuccessfully() {
        RegisterPatientCommand command = registerCommand();

        when(userRepository.findById(2L)).thenReturn(Optional.of(validUser()));
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(validPatient());

        Patient result = patientService.registerPatient(command);

        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getBloodGroup()).isEqualTo("O+");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundDuringPatientRegistration() {
        RegisterPatientCommand command = registerCommand();

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.registerPatient(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");

        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPatientProfileAlreadyExists() {
        RegisterPatientCommand command = registerCommand();

        when(userRepository.findById(2L)).thenReturn(Optional.of(validUser()));
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.of(validPatient()));

        assertThatThrownBy(() -> patientService.registerPatient(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("patient profile");

        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldGetPatientByIdSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(validPatient()));

        Patient result = patientService.getPatientById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenPatientNotFoundById() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");
    }

    @Test
    void shouldGetPatientByUserIdSuccessfully() {
        when(patientRepository.findByUserId(2L)).thenReturn(Optional.of(validPatient()));

        Patient result = patientService.getPatientByUserId(2L);

        assertThat(result.getUserId()).isEqualTo(2L);
    }

    @Test
    void shouldThrowWhenPatientNotFoundByUserId() {
        when(patientRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientByUserId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");
    }

    @Test
    void shouldUpdateAddressSuccessfully() {
        Patient patient = validPatient();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.updateAddress(1L, "456 Brigade Road, Bangalore");

        assertThat(result.getAddress()).isEqualTo("456 Brigade Road, Bangalore");
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowWhenUpdatingAddressOfNonExistentPatient() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updateAddress(99L, "New Address"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");
    }

    @Test
    void shouldUpdateEmergencyContactSuccessfully() {
        Patient patient = validPatient();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.updateEmergencyContact(1L, "1122334455");

        assertThat(result.getEmergencyContact()).isEqualTo("1122334455");
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowWhenUpdatingEmergencyContactOfNonExistentPatient() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updateEmergencyContact(99L, "1122334455"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");
    }
}