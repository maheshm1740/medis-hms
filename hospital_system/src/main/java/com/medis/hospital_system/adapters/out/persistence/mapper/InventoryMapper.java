package com.medis.hospital_system.adapters.out.persistence.mapper;

import com.medis.hospital_system.adapters.out.persistence.entity.InventoryEntity;
import com.medis.hospital_system.adapters.out.persistence.entity.MedicineEntity;
import com.medis.hospital_system.domain.model.Inventory;

public final class InventoryMapper {

    private InventoryMapper() {}

    public static InventoryEntity toEntity(Inventory inventory) {

        if (inventory == null) {
            return null;
        }

        InventoryEntity entity = new InventoryEntity();

        entity.setId(inventory.getId());

        MedicineEntity medicine = new MedicineEntity();
        medicine.setId(inventory.getMedicineId());
        entity.setMedicine(medicine);

        entity.setStockQuantity(inventory.getStockQuantity());
        entity.setReorderLevel(inventory.getReorderLevel());

        return entity;
    }

    public static Inventory toDomain(InventoryEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Inventory(
                entity.getId(),
                entity.getMedicine().getId(),
                entity.getStockQuantity(),
                entity.getReorderLevel()
        );
    }
}
