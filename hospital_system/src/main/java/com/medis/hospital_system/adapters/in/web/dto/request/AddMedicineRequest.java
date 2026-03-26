package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddMedicineRequest(

        @NotBlank(message = "Medicine name is required")
        String name,

        @NotBlank(message = "Manufacturer is required")
        String manufacturer,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
        BigDecimal price
) {}