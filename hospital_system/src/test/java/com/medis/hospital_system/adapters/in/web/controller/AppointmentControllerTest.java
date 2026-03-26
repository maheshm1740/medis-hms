package com.medis.hospital_system.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medis.hospital_system.application.command.BookAppointmentCommand;
import com.medis.hospital_system.application.command.RescheduleAppointmentCommand;
import com.medis.hospital_system.application.port.in.AppointmentUseCase;
import com.medis.hospital_system.domain.exception.AppointmentConflictException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import com.medis.hospital_system.infrastructure.config.TestSecurityConfig;
import com.medis.hospital_system.infrastructure.exception.GlobalExceptionHandler;
import com.medis.hospital_system.infrastructure.security.JwtAuthenticationFilter;
import com.medis.hospital_system.infrastructure.security.JwtTokenProvider;
import com.medis.hospital_system.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentUseCase appointmentUseCase;

//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private final LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

    private Appointment validAppointment() {
        return new Appointment(1L, 1L, 1L, futureTime,
                AppointmentStatus.SCHEDULED, "Regular checkup");
    }

    // ── Book ──────────────────────────────────────────────────────────────────

    @Test
    void shouldBookAppointmentAndReturn201() throws Exception {
        when(appointmentUseCase.bookAppointment(any(BookAppointmentCommand.class)))
                .thenReturn(validAppointment());

        // Use a far-future date so @Future validation always passes
        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": 1,
                                  "doctorId": 1,
                                  "appointmentTime": "2099-12-01T10:00:00",
                                  "notes": "Regular checkup"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.notes").value("Regular checkup"));
    }

    @Test
    void shouldReturn409WhenDoctorHasConflictingAppointment() throws Exception {
        when(appointmentUseCase.bookAppointment(any(BookAppointmentCommand.class)))
                .thenThrow(new AppointmentConflictException(
                        "Doctor already has an appointment at this time"));

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": 1,
                                  "doctorId": 1,
                                  "appointmentTime": "2099-12-01T10:00:00",
                                  "notes": "Regular checkup"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Doctor already has an appointment at this time"));
    }

    @Test
    void shouldReturn400WhenBookingRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": null,
                                  "doctorId": null,
                                  "appointmentTime": null,
                                  "notes": "Regular checkup"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // ── Get by ID ─────────────────────────────────────────────────────────────

    @Test
    void shouldGetAppointmentByIdAndReturn200() throws Exception {
        when(appointmentUseCase.getAppointmentById(1L))
                .thenReturn(validAppointment());

        mockMvc.perform(get("/api/v1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void shouldReturn404WhenAppointmentNotFoundById() throws Exception {
        when(appointmentUseCase.getAppointmentById(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Appointment not found with id: 99"));

        mockMvc.perform(get("/api/v1/appointments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Appointment not found with id: 99"));
    }

    // ── Get by doctor / patient / status ──────────────────────────────────────

    @Test
    void shouldGetAppointmentsByDoctorAndReturn200() throws Exception {
        when(appointmentUseCase.getAppointmentsByDoctor(1L))
                .thenReturn(List.of(validAppointment()));

        mockMvc.perform(get("/api/v1/appointments/by-doctor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(1));
    }

    @Test
    void shouldGetAppointmentsByPatientAndReturn200() throws Exception {
        when(appointmentUseCase.getAppointmentsByPatient(1L))
                .thenReturn(List.of(validAppointment()));

        mockMvc.perform(get("/api/v1/appointments/by-patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].patientId").value(1));
    }

    @Test
    void shouldGetAppointmentsByStatusAndReturn200() throws Exception {
        when(appointmentUseCase.getAppointmentsByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(validAppointment()));

        mockMvc.perform(get("/api/v1/appointments/by-status")
                        .param("status", "SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    // ── Complete / Cancel ─────────────────────────────────────────────────────

    @Test
    void shouldCompleteAppointmentAndReturn200() throws Exception {
        Appointment completed = new Appointment(1L, 1L, 1L, futureTime,
                AppointmentStatus.COMPLETED, "Regular checkup");
        when(appointmentUseCase.completeAppointment(1L)).thenReturn(completed);

        mockMvc.perform(patch("/api/v1/appointments/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldCancelAppointmentAndReturn200() throws Exception {
        Appointment cancelled = new Appointment(1L, 1L, 1L, futureTime,
                AppointmentStatus.CANCELLED, "Regular checkup");
        when(appointmentUseCase.cancelAppointment(1L)).thenReturn(cancelled);

        mockMvc.perform(patch("/api/v1/appointments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldReturn409WhenCancellingNonScheduledAppointment() throws Exception {
        when(appointmentUseCase.cancelAppointment(1L))
                .thenThrow(new IllegalStateException(
                        "Only scheduled appointments can be cancelled"));

        mockMvc.perform(patch("/api/v1/appointments/1/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Only scheduled appointments can be cancelled"));
    }

    // ── Reschedule ────────────────────────────────────────────────────────────

    @Test
    void shouldRescheduleAppointmentAndReturn200() throws Exception {
        Appointment rescheduled = new Appointment(1L, 1L, 1L,
                futureTime.plusDays(2), AppointmentStatus.SCHEDULED, "Regular checkup");
        when(appointmentUseCase.rescheduleAppointment(any(RescheduleAppointmentCommand.class)))
                .thenReturn(rescheduled);

        // Far-future date so @Future validation always passes
        mockMvc.perform(patch("/api/v1/appointments/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newAppointmentTime": "2099-12-05T14:00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void shouldReturn409WhenReschedulingToConflictingTime() throws Exception {
        when(appointmentUseCase.rescheduleAppointment(any(RescheduleAppointmentCommand.class)))
                .thenThrow(new AppointmentConflictException(
                        "Doctor already has an appointment at the requested new time"));

        mockMvc.perform(patch("/api/v1/appointments/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newAppointmentTime": "2099-12-05T14:00:00"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Doctor already has an appointment at the requested new time"));
    }
}
