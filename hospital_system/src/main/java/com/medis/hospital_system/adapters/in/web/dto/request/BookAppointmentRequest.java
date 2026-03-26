package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookAppointmentRequest(

        @NotNull(message = "Patient ID is required")
        Long patientId,

        @NotNull(message = "Doctor ID is required")
        Long doctorId,

        @NotNull(message = "Appointment time is required")
        LocalDateTime appointmentTime,

        String notes
) {}
