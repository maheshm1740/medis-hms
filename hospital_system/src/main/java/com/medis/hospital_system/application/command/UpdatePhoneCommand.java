package com.medis.hospital_system.application.command;

public record UpdatePhoneCommand(
        Long userId,
        String newPhone
) {}