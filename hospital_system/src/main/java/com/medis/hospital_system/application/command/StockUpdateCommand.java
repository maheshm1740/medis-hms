package com.medis.hospital_system.application.command;

public record StockUpdateCommand(
        Long medicineId,
        int quantity
) {}
