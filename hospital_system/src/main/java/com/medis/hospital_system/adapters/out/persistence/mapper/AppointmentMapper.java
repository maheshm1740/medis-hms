package com.medis.hospital_system.adapters.out.persistence.mapper;

import com.medis.hospital_system.adapters.out.persistence.entity.AppointmentEntity;
import com.medis.hospital_system.adapters.out.persistence.entity.DoctorEntity;
import com.medis.hospital_system.adapters.out.persistence.entity.PatientEntity;
import com.medis.hospital_system.domain.model.Appointment;

public final class AppointmentMapper {

    private AppointmentMapper() {}

    public static AppointmentEntity toEntity(Appointment appointment) {

        if (appointment == null) {
            return null;
        }

        AppointmentEntity entity = new AppointmentEntity();

        entity.setId(appointment.getId());

        // convert patientId -> PatientEntity
        PatientEntity patient = new PatientEntity();
        patient.setId(appointment.getPatientId());
        entity.setPatient(patient);

        // convert doctorId -> DoctorEntity
        DoctorEntity doctor = new DoctorEntity();
        doctor.setId(appointment.getDoctorId());
        entity.setDoctor(doctor);

        entity.setAppointmentTime(appointment.getAppointmentTime());
        entity.setStatus(appointment.getStatus());
        entity.setNotes(appointment.getNotes());

        return entity;
    }

    public static Appointment toDomain(AppointmentEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Appointment(
                entity.getId(),
                entity.getPatient().getId(),
                entity.getDoctor().getId(),
                entity.getAppointmentTime(),
                entity.getStatus(),
                entity.getNotes()
        );
    }
}