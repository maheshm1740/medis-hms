package com.medis.hospital_system.adapters.out.persistence.mapper;

import com.medis.hospital_system.adapters.out.persistence.entity.DoctorEntity;
import com.medis.hospital_system.adapters.out.persistence.entity.UserEntity;
import com.medis.hospital_system.domain.model.Doctor;

public final class DoctorMapper {

    private DoctorMapper() {}

    public static DoctorEntity toEntity(Doctor doctor) {

        if (doctor == null) {
            return null;
        }

        DoctorEntity entity = new DoctorEntity();

        entity.setId(doctor.getId());

        UserEntity user = new UserEntity();
        user.setId(doctor.getUserId());
        entity.setUser(user);

        entity.setSpecialization(doctor.getSpecialization());
        entity.setDepartment(doctor.getDepartment());
        entity.setLicenseNumber(doctor.getLicenseNumber());
        entity.setExperienceYears(doctor.getExperienceYears());

        return entity;
    }

    public static Doctor toDomain(DoctorEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Doctor(
                entity.getId(),
                entity.getUser().getId(),
                entity.getSpecialization(),
                entity.getDepartment(),
                entity.getLicenseNumber(),
                entity.getExperienceYears()
        );
    }
}