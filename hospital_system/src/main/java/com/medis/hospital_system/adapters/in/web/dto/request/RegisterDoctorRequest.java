package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RegisterDoctorRequest(

        @NotBlank(message = "Email is required")
        String email,

        @NotEmpty(message = "At least one specialization is required")
        List<String> specialization,

        @NotBlank(message = "Department is required")
        String department,

        @NotBlank(message = "License number is required")
        String licenseNumber,

        @Min(value = 0, message = "Experience years cannot be negative")
        int experienceYears
) {}