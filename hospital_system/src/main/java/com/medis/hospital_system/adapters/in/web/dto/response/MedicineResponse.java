package com.medis.hospital_system.adapters.in.web.dto.response;

import com.medis.hospital_system.domain.model.Medicine;

import java.math.BigDecimal;

public record MedicineResponse(
        Long id,
        String name,
        String manufacturer,
        BigDecimal price
) {
    public static MedicineResponse from(Medicine medicine) {
        return new MedicineResponse(
                medicine.getId(),
                medicine.getName(),
                medicine.getManufacturer(),
                medicine.getPrice()
        );
    }
}