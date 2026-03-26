package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(

        @NotBlank(message = "Old password is required")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword
) {}