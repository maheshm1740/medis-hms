package com.medis.hospital_system.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PatientTest {

    private Patient validPatient() {
        return new Patient(1L, 1L, "O+", "123 MG Road, Bangalore", "9988776655");
    }

    @Test
    void shouldCreatePatientSuccessfully() {
        Patient patient = validPatient();

        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getUserId()).isEqualTo(1L);
        assertThat(patient.getBloodGroup()).isEqualTo("O+");
        assertThat(patient.getAddress()).isEqualTo("123 MG Road, Bangalore");
        assertThat(patient.getEmergencyContact()).isEqualTo("9988776655");
    }

    @Test
    void shouldThrowWhenUserIdIsNull() {
        assertThatThrownBy(() ->
                new Patient(1L, null, "O+", "123 MG Road", "9988776655"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("user");
    }

    @Test
    void shouldThrowWhenBloodGroupIsInvalid() {
        assertThatThrownBy(() ->
                new Patient(1L, 1L, "X+", "123 MG Road", "9988776655"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blood group");
    }

    @Test
    void shouldThrowWhenBloodGroupIsNull() {
        assertThatThrownBy(() ->
                new Patient(1L, 1L, null, "123 MG Road", "9988776655"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blood group");
    }

    @Test
    void shouldThrowWhenAddressIsBlank() {
        assertThatThrownBy(() ->
                new Patient(1L, 1L, "O+", "", "9988776655"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Address");
    }

    @Test
    void shouldThrowWhenEmergencyContactIsBlank() {
        assertThatThrownBy(() ->
                new Patient(1L, 1L, "O+", "123 MG Road", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Emergency contact");
    }

    @Test
    void shouldUpdateAddressSuccessfully() {
        Patient patient = validPatient();
        patient.updateAddress("456 Brigade Road, Bangalore");
        assertThat(patient.getAddress()).isEqualTo("456 Brigade Road, Bangalore");
    }

    @Test
    void shouldThrowWhenUpdatingAddressToBlank() {
        Patient patient = validPatient();
        assertThatThrownBy(() -> patient.updateAddress(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Address");
    }

    @Test
    void shouldUpdateEmergencyContactSuccessfully() {
        Patient patient = validPatient();
        patient.updateEmergencyContact("1122334455");
        assertThat(patient.getEmergencyContact()).isEqualTo("1122334455");
    }

    @Test
    void shouldThrowWhenUpdatingEmergencyContactToBlank() {
        Patient patient = validPatient();
        assertThatThrownBy(() -> patient.updateEmergencyContact(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Emergency contact");
    }

    @Test
    void shouldReturnTrueWhenEmergencyContactExists() {
        Patient patient = validPatient();
        assertThat(patient.hasEmergencyContact()).isTrue();
    }

    @Test
    void shouldAcceptAllValidBloodGroups() {
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String bloodGroup : validGroups) {
            assertThatNoException().isThrownBy(() ->
                    new Patient(1L, 1L, bloodGroup, "123 MG Road", "9988776655"));
        }
    }
}