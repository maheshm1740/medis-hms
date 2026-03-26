package com.medis.hospital_system.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DoctorTest {

    private Doctor validDoctor() {
        return new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    @Test
    void shouldCreateDoctorSuccessfully() {
        Doctor doctor = validDoctor();

        assertThat(doctor.getId()).isEqualTo(1L);
        assertThat(doctor.getUserId()).isEqualTo(1L);
        assertThat(doctor.getSpecialization()).containsExactly("Cardiology");
        assertThat(doctor.getDepartment()).isEqualTo("Cardiology");
        assertThat(doctor.getLicenseNumber()).isEqualTo("KA-MED-001");
        assertThat(doctor.getExperienceYears()).isEqualTo(12);
    }

    @Test
    void shouldThrowWhenUserIdIsNull() {
        assertThatThrownBy(() ->
                new Doctor(1L, null, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("user");
    }

    @Test
    void shouldThrowWhenSpecializationIsEmpty() {
        assertThatThrownBy(() ->
                new Doctor(1L, 1L, List.of(), "Cardiology", "KA-MED-001", 12))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Specialization");
    }

    @Test
    void shouldThrowWhenDepartmentIsBlank() {
        assertThatThrownBy(() ->
                new Doctor(1L, 1L, List.of("Cardiology"), "", "KA-MED-001", 12))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Department");
    }

    @Test
    void shouldThrowWhenLicenseNumberIsBlank() {
        assertThatThrownBy(() ->
                new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "", 12))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("License");
    }

    @Test
    void shouldThrowWhenExperienceYearsIsNegative() {
        assertThatThrownBy(() ->
                new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Experience");
    }

    @Test
    void shouldUpdateDepartmentSuccessfully() {
        Doctor doctor = validDoctor();
        doctor.updateDepartment("Neurology");
        assertThat(doctor.getDepartment()).isEqualTo("Neurology");
    }

    @Test
    void shouldThrowWhenUpdatingDepartmentToBlank() {
        Doctor doctor = validDoctor();
        assertThatThrownBy(() -> doctor.updateDepartment(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Department");
    }

    @Test
    void shouldUpdateSpecializationSuccessfully() {
        Doctor doctor = validDoctor();
        doctor.updateSpecialization(List.of("Neurology", "Psychiatry"));
        assertThat(doctor.getSpecialization()).containsExactly("Neurology", "Psychiatry");
    }

    @Test
    void shouldThrowWhenUpdatingSpecializationToEmpty() {
        Doctor doctor = validDoctor();
        assertThatThrownBy(() -> doctor.updateSpecialization(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Specialization");
    }

    @Test
    void shouldReturnTrueForExperiencedDoctor() {
        Doctor experienced = new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 10);
        Doctor junior = new Doctor(2L, 2L, List.of("Cardiology"), "Cardiology", "KA-MED-002", 5);

        assertThat(experienced.isExperiencedDoctor()).isTrue();
        assertThat(junior.isExperiencedDoctor()).isFalse();
    }
}