package com.medis.hospital_system.adapters.in.web.dto.request;

import com.medis.hospital_system.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStaffRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String phone,
        @NotNull Role role
) {}