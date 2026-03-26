package com.medis.hospital_system.application.command;

import java.time.LocalDateTime;

public record BookAppointmentCommand(
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentTime,
        String notes
) {}