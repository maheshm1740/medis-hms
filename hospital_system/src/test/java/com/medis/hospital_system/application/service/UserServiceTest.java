package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.RegisterUserCommand;
import com.medis.hospital_system.application.command.UpdatePasswordCommand;
import com.medis.hospital_system.application.command.UpdatePhoneCommand;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User validPatient() {
        return new User(1L, "John Doe", "john@example.com", "encodedPassword", "9999999999", Role.PATIENT, true);
    }

    private User validDoctor() {
        return new User(2L, "Dr. Smith", "smith@example.com", "encodedPassword", "8888888888", Role.DOCTOR, false);
    }

    private RegisterUserCommand patientRegisterCommand() {
        return new RegisterUserCommand("John Doe", "john@example.com", "password123", "9999999999", Role.PATIENT, true);
    }

    private RegisterUserCommand doctorRegisterCommand() {
        return new RegisterUserCommand("Dr. Smith", "smith@example.com", "temp123", "8888888888", Role.DOCTOR, false);
    }

    // ─── Register ────────────────────────────────────────────────────────────

    @Test
    void shouldRegisterPatientSuccessfully() {
        RegisterUserCommand command = patientRegisterCommand();
        User user = validPatient();

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(command);

        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getRole()).isEqualTo(Role.PATIENT);
        assertThat(result.isPasswordChanged()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRegisterDoctorWithPasswordChangedFalse() {
        RegisterUserCommand command = doctorRegisterCommand();
        User user = validDoctor();

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(command);

        assertThat(result.getRole()).isEqualTo(Role.DOCTOR);
        assertThat(result.isPasswordChanged()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        RegisterUserCommand command = patientRegisterCommand();

        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any());
    }

    // ─── Get by ID ───────────────────────────────────────────────────────────

    @Test
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(validPatient()));

        User result = userService.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    // ─── Get by Email ─────────────────────────────────────────────────────────

    @Test
    void shouldGetUserByEmailSuccessfully() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(validPatient()));

        User result = userService.getUserByEmail("john@example.com");

        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Update Phone ─────────────────────────────────────────────────────────

    @Test
    void shouldUpdatePhoneSuccessfully() {
        UpdatePhoneCommand command = new UpdatePhoneCommand(1L, "8888888888");
        User user = validPatient();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updatePhone(command);

        assertThat(result.getPhone()).isEqualTo("8888888888");
        verify(userRepository).save(user);
    }

    // ─── Update Password ──────────────────────────────────────────────────────

    @Test
    void shouldUpdatePasswordSuccessfully() {
        UpdatePasswordCommand command = new UpdatePasswordCommand(1L, "oldPassword123", "newPassword123");
        User user = validPatient();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword123", user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updatePassword(command);

        verify(userRepository).save(user);
        assertThat(user.isPasswordChanged()).isTrue();
    }

    @Test
    void shouldThrowWhenOldPasswordIsIncorrect() {
        UpdatePasswordCommand command = new UpdatePasswordCommand(1L, "wrongOldPassword", "newPassword123");
        User user = validPatient();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Old password is incorrect");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenNewPasswordSameAsOld() {
        UpdatePasswordCommand command = new UpdatePasswordCommand(1L, "password123", "password123");
        User user = validPatient();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New password cannot be same as old password");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldMarkPasswordChangedAfterUpdate() {
        UpdatePasswordCommand command = new UpdatePasswordCommand(1L, "oldPassword123", "newPassword123");
        User user = validDoctor(); // passwordChanged = false initially

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword123", user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThat(user.isPasswordChanged()).isFalse();
        userService.updatePassword(command);
        assertThat(user.isPasswordChanged()).isTrue();
    }

    // ─── Email Exists ─────────────────────────────────────────────────────────

    @Test
    void shouldReturnTrueWhenEmailExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThat(userService.emailExists("john@example.com")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        assertThat(userService.emailExists("unknown@example.com")).isFalse();
    }
}