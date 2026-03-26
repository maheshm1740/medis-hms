package com.medis.hospital_system.adapters.out.persistence.entity;

import com.medis.hospital_system.domain.model.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "appointments",
        indexes = {
                @Index(name = "idx_appointment_patient", columnList = "patient_id"),
                @Index(name = "idx_appointment_doctor", columnList = "doctor_id"),
                @Index(name = "idx_appointment_time", columnList = "appointment_time"),
                @Index(name = "idx_doctor_time", columnList = "doctor_id, appointment_time")
        }
)
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;

    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    private String notes;

    public AppointmentEntity() {}

    public AppointmentEntity(Long id,
                             PatientEntity patient,
                             DoctorEntity doctor,
                             LocalDateTime appointmentTime,
                             AppointmentStatus status,
                             String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }
}