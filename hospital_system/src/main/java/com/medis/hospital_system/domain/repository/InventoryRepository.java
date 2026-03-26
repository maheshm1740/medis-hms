package com.medis.hospital_system.domain.repository;

import com.medis.hospital_system.domain.model.Inventory;

import java.util.Optional;

public interface InventoryRepository {

    Inventory save(Inventory inventory);

    Optional<Inventory> findByMedicineId(Long medicineId);
}

