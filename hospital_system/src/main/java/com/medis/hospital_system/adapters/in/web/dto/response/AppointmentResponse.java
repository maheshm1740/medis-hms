package com.medis.hospital_system.adapters.in.web.dto.response;

import com.medis.hospital_system.application.dto.AppointmentDetails;
import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        LocalDateTime appointmentTime,
        AppointmentStatus status,
        String notes
) {
    public static AppointmentResponse from(AppointmentDetails d) {
        return new AppointmentResponse(
                d.id(),
                d.patientId(),
                d.patientName(),
                d.doctorId(),
                d.doctorName(),
                d.appointmentTime(),
                d.status(),
                d.notes()
        );
    }
}