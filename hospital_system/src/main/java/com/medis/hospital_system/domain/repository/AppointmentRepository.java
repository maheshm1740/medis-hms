package com.medis.hospital_system.domain.repository;

import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(Long id);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findAll();

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end); // added — replaces broken date query
}