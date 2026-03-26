package com.medis.hospital_system.adapters.in.web.dto.response;

import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        Role role,
        boolean passwordChanged
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.isPasswordChanged()
        );
    }
}