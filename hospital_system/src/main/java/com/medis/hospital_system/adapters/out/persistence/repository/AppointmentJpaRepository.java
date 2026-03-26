package com.medis.hospital_system.adapters.out.persistence.repository;

import com.medis.hospital_system.adapters.out.persistence.entity.AppointmentEntity;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByDoctorId(Long doctorId);

    List<AppointmentEntity> findByPatientId(Long patientId);

    List<AppointmentEntity> findByStatus(AppointmentStatus status);

    // Fixed: was findByDoctorIdAndAppointmentDate(LocalDate) — field doesn't exist.
    // Correct field is appointmentTime (LocalDateTime), so we query a time range.
    List<AppointmentEntity> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
}