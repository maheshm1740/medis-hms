package com.medis.hospital_system.adapters.out.persistence.mapper;

import com.medis.hospital_system.adapters.out.persistence.entity.PatientEntity;
import com.medis.hospital_system.adapters.out.persistence.entity.UserEntity;
import com.medis.hospital_system.domain.model.Patient;

public final class PatientMapper {

    private PatientMapper() {}

    public static PatientEntity toEntity(Patient patient) {

        if (patient == null) {
            return null;
        }

        PatientEntity entity = new PatientEntity();

        entity.setId(patient.getId());

        UserEntity user = new UserEntity();
        user.setId(patient.getUserId());
        entity.setUser(user);

        entity.setBloodGroup(patient.getBloodGroup());
        entity.setAddress(patient.getAddress());
        entity.setEmergencyContact(patient.getEmergencyContact());

        return entity;
    }

    public static Patient toDomain(PatientEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Patient(
                entity.getId(),
                entity.getUser().getId(),
                entity.getBloodGroup(),
                entity.getAddress(),
                entity.getEmergencyContact()
        );
    }
}