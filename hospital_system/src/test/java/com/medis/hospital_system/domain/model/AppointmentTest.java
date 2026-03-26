package com.medis.hospital_system.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class AppointmentTest {

    private final LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

    private Appointment validAppointment() {
        return new Appointment(1L, 1L, 1L, futureTime, AppointmentStatus.SCHEDULED, "Regular checkup");
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
        Appointment appointment = validAppointment();

        assertThat(appointment.getId()).isEqualTo(1L);
        assertThat(appointment.getPatientId()).isEqualTo(1L);
        assertThat(appointment.getDoctorId()).isEqualTo(1L);
        assertThat(appointment.getAppointmentTime()).isEqualTo(futureTime);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(appointment.getNotes()).isEqualTo("Regular checkup");
    }

    @Test
    void shouldThrowWhenPatientIdIsNull() {
        assertThatThrownBy(() ->
                new Appointment(1L, null, 1L, futureTime, AppointmentStatus.SCHEDULED, "notes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient");
    }

    @Test
    void shouldThrowWhenDoctorIdIsNull() {
        assertThatThrownBy(() ->
                new Appointment(1L, 1L, null, futureTime, AppointmentStatus.SCHEDULED, "notes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Doctor");
    }

    @Test
    void shouldThrowWhenAppointmentTimeIsNull() {
        assertThatThrownBy(() ->
                new Appointment(1L, 1L, 1L, null, AppointmentStatus.SCHEDULED, "notes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("time");
    }

    @Test
    void shouldThrowWhenStatusIsNull() {
        assertThatThrownBy(() ->
                new Appointment(1L, 1L, 1L, futureTime, null, "notes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("status");
    }

    @Test
    void shouldCompleteAppointmentSuccessfully() {
        Appointment appointment = validAppointment();
        appointment.completeAppointment();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void shouldThrowWhenCompletingAlreadyCompletedAppointment() {
        Appointment appointment = validAppointment();
        appointment.completeAppointment();
        assertThatThrownBy(() -> appointment.completeAppointment())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scheduled");
    }

    @Test
    void shouldCancelAppointmentSuccessfully() {
        Appointment appointment = validAppointment();
        appointment.cancelAppointment();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    void shouldThrowWhenCancellingAlreadyCancelledAppointment() {
        Appointment appointment = validAppointment();
        appointment.cancelAppointment();
        assertThatThrownBy(() -> appointment.cancelAppointment())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scheduled");
    }

    @Test
    void shouldRescheduleAppointmentSuccessfully() {
        Appointment appointment = validAppointment();
        LocalDateTime newTime = futureTime.plusDays(2);
        appointment.reschedule(newTime);
        assertThat(appointment.getAppointmentTime()).isEqualTo(newTime);
    }

    @Test
    void shouldThrowWhenReschedulingCompletedAppointment() {
        Appointment appointment = validAppointment();
        appointment.completeAppointment();
        assertThatThrownBy(() -> appointment.reschedule(futureTime.plusDays(2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scheduled");
    }

    @Test
    void shouldThrowWhenReschedulingCancelledAppointment() {
        Appointment appointment = validAppointment();
        appointment.cancelAppointment();
        assertThatThrownBy(() -> appointment.reschedule(futureTime.plusDays(2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scheduled");
    }

    @Test
    void shouldThrowWhenReschedulingWithNullTime() {
        Appointment appointment = validAppointment();
        assertThatThrownBy(() -> appointment.reschedule(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("time");
    }
}