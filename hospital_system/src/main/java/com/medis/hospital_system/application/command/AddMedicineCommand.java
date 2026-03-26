package com.medis.hospital_system.application.command;

import java.math.BigDecimal;

public record AddMedicineCommand(
        String name,
        String manufacturer,
        BigDecimal price
) {}