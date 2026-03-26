package com.medis.hospital_system.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class Appointment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String notes;

    @JsonCreator
    public Appointment(@JsonProperty("id")              Long id,
                       @JsonProperty("patientId")       Long patientId,
                       @JsonProperty("doctorId")        Long doctorId,
                       @JsonProperty("appointmentTime") LocalDateTime appointmentTime,
                       @JsonProperty("status")          AppointmentStatus status,
                       @JsonProperty("notes")           String notes) {

        if (patientId == null) {
            throw new IllegalArgumentException("Patient is required");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor is required");
        }
        if (appointmentTime == null) {
            throw new IllegalArgumentException("Appointment time is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Appointment status is required");
        }

        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    public void completeAppointment() {
        if (this.status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be completed");
        }
        this.status = AppointmentStatus.COMPLETED;
    }

    public void cancelAppointment() {
        if (this.status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be cancelled");
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    public void reschedule(LocalDateTime newTime) {
        if (this.status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be rescheduled");
        }
        if (newTime == null) {
            throw new IllegalArgumentException("New appointment time required");
        }
        this.appointmentTime = newTime;
    }
}