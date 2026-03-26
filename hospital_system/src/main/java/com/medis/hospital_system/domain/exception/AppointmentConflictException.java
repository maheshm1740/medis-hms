package com.medis.hospital_system.domain.exception;

public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}