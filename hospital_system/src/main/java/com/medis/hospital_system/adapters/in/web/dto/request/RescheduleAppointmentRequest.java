package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleAppointmentRequest(

        @NotNull(message = "New appointment time is required")
        LocalDateTime newAppointmentTime
) {}
