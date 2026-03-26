package com.medis.hospital_system.adapters.in.web.dto.response;

import com.medis.hospital_system.application.dto.DoctorDetails;

import java.util.List;

public record DoctorResponse(
        Long id,
        Long userId,
        String name,
        String email,          // ← add this
        List<String> specialization,
        String department,
        String licenseNumber,
        int experienceYears,
        boolean experiencedDoctor
) {
    public static DoctorResponse from(DoctorDetails d) {
        return new DoctorResponse(
                d.id(),
                d.userId(),
                d.name(),
                d.email(),     // ← add this
                d.specialization(),
                d.department(),
                d.licenseNumber(),
                d.experienceYears(),
                d.experiencedDoctor()
        );
    }
}