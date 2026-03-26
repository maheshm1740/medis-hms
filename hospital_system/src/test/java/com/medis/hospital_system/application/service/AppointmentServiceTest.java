package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.BookAppointmentCommand;
import com.medis.hospital_system.application.command.RescheduleAppointmentCommand;
import com.medis.hospital_system.domain.exception.AppointmentConflictException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import com.medis.hospital_system.domain.model.Doctor;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.domain.repository.AppointmentRepository;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import com.medis.hospital_system.domain.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private final LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

    private Appointment validAppointment() {
        return new Appointment(1L, 1L, 1L, futureTime, AppointmentStatus.SCHEDULED, "Regular checkup");
    }

    private Doctor validDoctor() {
        return new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    private Patient validPatient() {
        return new Patient(1L, 2L, "O+", "123 MG Road", "9988776655");
    }

    private BookAppointmentCommand bookCommand() {
        return new BookAppointmentCommand(1L, 1L, futureTime, "Regular checkup");
    }

    @Test
    void shouldBookAppointmentSuccessfully() {
        BookAppointmentCommand command = bookCommand();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(validPatient()));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(validAppointment());

        Appointment result = appointmentService.bookAppointment(command);

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowWhenDoctorNotFoundDuringBooking() {
        BookAppointmentCommand command = bookCommand();

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPatientNotFoundDuringBooking() {
        BookAppointmentCommand command = bookCommand();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAppointmentTimeIsInThePast() {
        BookAppointmentCommand command = new BookAppointmentCommand(
                1L, 1L, LocalDateTime.now().minusDays(1), "notes");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(validPatient()));

        assertThatThrownBy(() -> appointmentService.bookAppointment(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("past");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDoctorHasConflictingAppointment() {
        BookAppointmentCommand command = bookCommand();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor()));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(validPatient()));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of(validAppointment()));

        assertThatThrownBy(() -> appointmentService.bookAppointment(command))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("already has an appointment");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldGetAppointmentByIdSuccessfully() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(validAppointment()));

        Appointment result = appointmentService.getAppointmentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenAppointmentNotFoundById() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment");
    }

    @Test
    void shouldGetAppointmentsByDoctorSuccessfully() {
        when(appointmentRepository.findByDoctorId(1L)).thenReturn(List.of(validAppointment()));

        List<Appointment> result = appointmentService.getAppointmentsByDoctor(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldGetAppointmentsByPatientSuccessfully() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(validAppointment()));

        List<Appointment> result = appointmentService.getAppointmentsByPatient(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldGetAppointmentsByStatusSuccessfully() {
        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(validAppointment()));

        List<Appointment> result = appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void shouldCompleteAppointmentSuccessfully() {
        Appointment appointment = validAppointment();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.completeAppointment(1L);

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldCancelAppointmentSuccessfully() {
        Appointment appointment = validAppointment();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(1L);

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldRescheduleAppointmentSuccessfully() {
        Appointment appointment = validAppointment();
        LocalDateTime newTime = futureTime.plusDays(2);
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(1L, newTime);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.rescheduleAppointment(command);

        assertThat(result.getAppointmentTime()).isEqualTo(newTime);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldThrowWhenReschedulingToConflictingTime() {
        Appointment appointment = validAppointment();
        LocalDateTime newTime = futureTime.plusDays(2);
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(1L, newTime);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of(validAppointment()));

        assertThatThrownBy(() -> appointmentService.rescheduleAppointment(command))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("already has an appointment");
    }

    @Test
    void shouldThrowWhenReschedulingToPastTime() {
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(
                1L, LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> appointmentService.rescheduleAppointment(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("past");
    }
}