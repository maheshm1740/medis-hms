package com.medis.hospital_system.application.command;

public record RegisterPatientCommand(
        String email,
        String bloodGroup,
        String address,
        String emergencyContact
) {}