package com.medis.hospital_system.adapters.out.persistence.adapter;

import com.medis.hospital_system.adapters.out.persistence.mapper.AppointmentMapper;
import com.medis.hospital_system.adapters.out.persistence.repository.AppointmentJpaRepository;
import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import com.medis.hospital_system.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AppointmentRepositoryAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentJpaRepository;

    public AppointmentRepositoryAdapter(AppointmentJpaRepository appointmentJpaRepository) {
        this.appointmentJpaRepository = appointmentJpaRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return AppointmentMapper.toDomain(
                appointmentJpaRepository.save(AppointmentMapper.toEntity(appointment))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> findById(Long id) {
        return appointmentJpaRepository.findById(id)
                .map(AppointmentMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByDoctorId(Long doctorId) {
        return appointmentJpaRepository.findByDoctorId(doctorId)
                .stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentJpaRepository.findByPatientId(patientId)
                .stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByStatus(AppointmentStatus status) {
        return appointmentJpaRepository.findByStatus(status)
                .stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentJpaRepository.findAll()
                .stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentJpaRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                .stream()
                .map(AppointmentMapper::toDomain)
                .toList();
    }
}