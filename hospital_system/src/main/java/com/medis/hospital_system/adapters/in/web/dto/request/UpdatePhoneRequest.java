package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePhoneRequest(

        @NotBlank(message = "Phone number is required")
        String phone
) {}