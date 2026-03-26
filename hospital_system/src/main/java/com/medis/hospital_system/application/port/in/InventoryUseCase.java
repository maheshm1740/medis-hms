package com.medis.hospital_system.application.port.in;

import com.medis.hospital_system.application.command.CreateInventoryCommand;
import com.medis.hospital_system.application.command.StockUpdateCommand;
import com.medis.hospital_system.domain.model.Inventory;

public interface InventoryUseCase {

    Inventory createInventory(CreateInventoryCommand command);

    Inventory getInventoryByMedicineId(Long medicineId);

    Inventory addStock(StockUpdateCommand command);

    Inventory deductStock(StockUpdateCommand command);

    Inventory updateReorderLevel(Long medicineId, int newReorderLevel);

    boolean isLowStock(Long medicineId);

    boolean hasStock(Long medicineId);
}