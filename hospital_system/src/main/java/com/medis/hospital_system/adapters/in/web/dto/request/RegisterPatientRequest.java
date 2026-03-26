package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterPatientRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,   // 🔥 changed

        @NotBlank(message = "Blood group is required")
        @Pattern(
                regexp = "^(A|B|AB|O)[+-]$",
                message = "Invalid blood group. Must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-"
        )
        String bloodGroup,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Emergency contact is required")
        String emergencyContact
) {}