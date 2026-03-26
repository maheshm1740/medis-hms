package com.medis.hospital_system.application.dto;

import java.util.List;

public record DoctorDetails(
        Long id,
        Long userId,
        String name,
        String email,          // ← added
        List<String> specialization,
        String department,
        String licenseNumber,
        int experienceYears,
        boolean experiencedDoctor
) {}