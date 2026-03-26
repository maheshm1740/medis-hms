package com.medis.hospital_system.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Long id) {
        return new ResourceNotFoundException(resource + " not found with id: " + id);
    }

    public static ResourceNotFoundException ofEmail(String email) {
        return new ResourceNotFoundException("User not found with email: " + email);
    }

    public static ResourceNotFoundException of(String resource, String identifier) {
        return new ResourceNotFoundException(resource + " not found with identifier: " + identifier);
    }
}