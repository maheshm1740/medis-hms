package com.medis.hospital_system.application.port.out;

import com.medis.hospital_system.domain.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {

    Inventory save(Inventory inventory);

    Optional<Inventory> findByMedicineId(Long medicineId);

    List<Inventory> findLowStock();
}