package com.medis.hospital_system.adapters.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryRequest(

        @NotNull(message = "Medicine ID is required")
        Long medicineId,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        @Min(value = 0, message = "Reorder level cannot be negative")
        int reorderLevel
) {}