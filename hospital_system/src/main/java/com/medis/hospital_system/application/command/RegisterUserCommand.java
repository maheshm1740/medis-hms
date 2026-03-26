package com.medis.hospital_system.application.command;

import com.medis.hospital_system.domain.model.Role;

public record RegisterUserCommand(
        String name,
        String email,
        String password,
        String phone,
        Role role,
        boolean passwordChanged
) {}