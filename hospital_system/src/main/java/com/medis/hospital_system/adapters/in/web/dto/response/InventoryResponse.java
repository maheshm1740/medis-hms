package com.medis.hospital_system.adapters.in.web.dto.response;

import com.medis.hospital_system.domain.model.Inventory;

public record InventoryResponse(
        Long id,
        Long medicineId,
        int stockQuantity,
        int reorderLevel,
        boolean lowStock
) {
    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getMedicineId(),
                inventory.getStockQuantity(),
                inventory.getReorderLevel(),
                inventory.isLowStock()
        );
    }
}