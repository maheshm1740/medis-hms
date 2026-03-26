package com.medis.hospital_system.adapters.out.persistence.mapper;

import com.medis.hospital_system.adapters.out.persistence.entity.MedicineEntity;
import com.medis.hospital_system.domain.model.Medicine;

public final class MedicineMapper {

    private MedicineMapper() {
    }

    public static MedicineEntity toEntity(Medicine medicine) {

        if (medicine == null) {
            return null;
        }

        MedicineEntity entity = new MedicineEntity();

        entity.setId(medicine.getId());
        entity.setName(medicine.getName());
        entity.setManufacturer(medicine.getManufacturer());
        entity.setPrice(medicine.getPrice());

        return entity;
    }

    public static Medicine toDomain(MedicineEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Medicine(
                entity.getId(),
                entity.getName(),
                entity.getManufacturer(),
                entity.getPrice()
        );
    }
}