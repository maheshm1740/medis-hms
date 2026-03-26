package com.medis.hospital_system.adapters.in.web.dto.response;

import com.medis.hospital_system.application.dto.PatientDetails;

public record PatientResponse(
        Long id,
        Long userId,
        String name,
        String bloodGroup,
        String address,
        String emergencyContact
) {
    public static PatientResponse from(PatientDetails p) {
        return new PatientResponse(
                p.id(),
                p.userId(),
                p.name(),
                p.bloodGroup(),
                p.address(),
                p.emergencyContact()
        );
    }
}