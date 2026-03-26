package com.medis.hospital_system.application.command;

public record CreateInventoryCommand(
        Long medicineId,
        int stockQuantity,
        int reorderLevel
) {}