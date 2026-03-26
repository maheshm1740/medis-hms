package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.CreateInventoryCommand;
import com.medis.hospital_system.application.command.StockUpdateCommand;
import com.medis.hospital_system.application.port.in.InventoryUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Inventory;
import com.medis.hospital_system.domain.repository.InventoryRepository;
import com.medis.hospital_system.domain.repository.MedicineRepository;
import com.medis.hospital_system.infrastructure.cache.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements InventoryUseCase {

    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            MedicineRepository medicineRepository) {
        this.inventoryRepository = inventoryRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    @CacheEvict(value = CacheNames.INVENTORY, key = "#command.medicineId()")
    public Inventory createInventory(CreateInventoryCommand command) {
        medicineRepository.findById(command.medicineId())
                .orElseThrow(() -> ResourceNotFoundException.of("Medicine", command.medicineId()));

        inventoryRepository.findByMedicineId(command.medicineId()).ifPresent(existing -> {
            throw new DuplicateResourceException("Inventory already exists for medicine id: " + command.medicineId());
        });

        Inventory inventory = new Inventory(
                null,
                command.medicineId(),
                command.stockQuantity(),
                command.reorderLevel()
        );
        return inventoryRepository.save(inventory);
    }

    @Override
    @Cacheable(value = CacheNames.INVENTORY, key = "#medicineId")
    public Inventory getInventoryByMedicineId(Long medicineId) {
        return inventoryRepository.findByMedicineId(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for medicine id: " + medicineId));
    }

    @Override
    @CachePut(value = CacheNames.INVENTORY, key = "#command.medicineId()")
    public Inventory addStock(StockUpdateCommand command) {
        Inventory inventory = getInventoryByMedicineId(command.medicineId());
        inventory.increaseStock(command.quantity());
        return inventoryRepository.save(inventory);
    }

    @Override
    @CachePut(value = CacheNames.INVENTORY, key = "#command.medicineId()")
    public Inventory deductStock(StockUpdateCommand command) {
        Inventory inventory = getInventoryByMedicineId(command.medicineId());
        inventory.decreaseStock(command.quantity());
        return inventoryRepository.save(inventory);
    }

    @Override
    @CachePut(value = CacheNames.INVENTORY, key = "#medicineId")
    public Inventory updateReorderLevel(Long medicineId, int newReorderLevel) {
        Inventory inventory = getInventoryByMedicineId(medicineId);
        inventory.updateReorderLevel(newReorderLevel);
        return inventoryRepository.save(inventory);
    }

    @Override
    public boolean isLowStock(Long medicineId) {
        return getInventoryByMedicineId(medicineId).isLowStock();
    }

    @Override
    public boolean hasStock(Long medicineId) {
        return getInventoryByMedicineId(medicineId).hasStock();
    }
}