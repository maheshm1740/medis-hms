package com.medis.hospital_system.adapters.in.web.dto.response;

public record AuthResponse(
        Long id,
        String accessToken,
        String refreshToken,
        String tokenType,
        String email,
        String role,
        boolean passwordChanged
) {
    public static AuthResponse of(Long id, String accessToken, String refreshToken,
                                  String email, String role, boolean passwordChanged) {
        return new AuthResponse(id, accessToken, refreshToken, "Bearer", email, role, passwordChanged);
    }
}