package com.medis.hospital_system.application.command;

import java.util.List;

public record RegisterDoctorCommand(
        String email,          // ← was Long userId
        List<String> specialization,
        String department,
        String licenseNumber,
        int experienceYears
) {}