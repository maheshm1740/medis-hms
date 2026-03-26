package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String refreshToken) {}