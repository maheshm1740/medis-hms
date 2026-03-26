package com.medis.hospital_system.application.dto;

public record PatientDetails(
        Long id,
        Long userId,
        String name,
        String bloodGroup,
        String address,
        String emergencyContact
) {}