package com.medis.hospital_system.domain.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}