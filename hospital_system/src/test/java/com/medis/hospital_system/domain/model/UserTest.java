package com.medis.hospital_system.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    private User validUser() {
        return new User(1L, "John Doe", "john@example.com", "password123", "9999999999", Role.PATIENT, true);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User user = validUser();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPhone()).isEqualTo("9999999999");
        assertThat(user.getRole()).isEqualTo(Role.PATIENT);
        assertThat(user.isPasswordChanged()).isTrue();
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() ->
                new User(1L, "", "john@example.com", "password123", "9999999999", Role.PATIENT, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        assertThatThrownBy(() ->
                new User(1L, "John", "invalid-email", "password123", "9999999999", Role.PATIENT, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void shouldThrowWhenPasswordIsTooShort() {
        assertThatThrownBy(() ->
                new User(1L, "John", "john@example.com", "abc", "9999999999", Role.PATIENT, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void shouldThrowWhenPhoneIsBlank() {
        assertThatThrownBy(() ->
                new User(1L, "John", "john@example.com", "password123", "", Role.PATIENT, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Phone");
    }

    @Test
    void shouldThrowWhenRoleIsNull() {
        assertThatThrownBy(() ->
                new User(1L, "John", "john@example.com", "password123", "9999999999", null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("role");
    }

    @Test
    void shouldChangePhoneSuccessfully() {
        User user = validUser();
        user.changePhone("8888888888");
        assertThat(user.getPhone()).isEqualTo("8888888888");
    }

    @Test
    void shouldThrowWhenChangingPhoneToBlank() {
        User user = validUser();
        assertThatThrownBy(() -> user.changePhone(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Phone");
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        User user = validUser();
        user.changePassword("newpassword123");
        assertThat(user.getPassword()).isEqualTo("newpassword123");
    }

    @Test
    void shouldThrowWhenChangingPasswordTooShort() {
        User user = validUser();
        assertThatThrownBy(() -> user.changePassword("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void shouldReturnCorrectRoleChecks() {
        User doctor = new User(1L, "John", "john@example.com", "password123", "9999999999", Role.DOCTOR, false);
        User patient = new User(2L, "Jane", "jane@example.com", "password123", "8888888888", Role.PATIENT, true);
        User admin = new User(3L, "Admin", "admin@example.com", "password123", "7777777777", Role.ADMIN, false);

        assertThat(doctor.isDoctor()).isTrue();
        assertThat(patient.isPatient()).isTrue();
        assertThat(admin.isAdmin()).isTrue();
    }

    @Test
    void shouldMarkPasswordChangedSuccessfully() {
        User user = new User(1L, "John", "john@example.com", "password123", "9999999999", Role.DOCTOR, false);
        assertThat(user.isPasswordChanged()).isFalse();
        user.markPasswordChanged();
        assertThat(user.isPasswordChanged()).isTrue();
    }

    @Test
    void shouldHavePasswordChangedFalseForAdminCreatedUser() {
        User doctor = new User(1L, "John", "john@example.com", "password123", "9999999999", Role.DOCTOR, false);
        assertThat(doctor.isPasswordChanged()).isFalse();
    }

    @Test
    void shouldHavePasswordChangedTrueForSelfRegisteredPatient() {
        User patient = new User(1L, "Jane", "jane@example.com", "password123", "8888888888", Role.PATIENT, true);
        assertThat(patient.isPasswordChanged()).isTrue();
    }
}