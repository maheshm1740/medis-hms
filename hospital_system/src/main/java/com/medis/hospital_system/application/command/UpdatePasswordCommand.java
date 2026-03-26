package com.medis.hospital_system.application.command;

public record UpdatePasswordCommand(
        Long userId,
        String oldPassword,
        String newPassword
) {}