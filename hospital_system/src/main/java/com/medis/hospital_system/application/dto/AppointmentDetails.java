package com.medis.hospital_system.application.dto;
import com.medis.hospital_system.domain.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentDetails(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        LocalDateTime appointmentTime,
        AppointmentStatus status,
        String notes
) {}