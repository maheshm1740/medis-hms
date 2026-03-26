package com.medis.hospital_system.application.command;

import java.time.LocalDateTime;

public record RescheduleAppointmentCommand(
        Long appointmentId,
        LocalDateTime newAppointmentTime
) {}