package com.medis.hospital_system.adapters.out.persistence.adapter;

import com.medis.hospital_system.adapters.out.persistence.mapper.InventoryMapper;
import com.medis.hospital_system.adapters.out.persistence.repository.InventoryJpaRepository;
import com.medis.hospital_system.domain.model.Inventory;
import com.medis.hospital_system.domain.repository.InventoryRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryRepositoryAdapter(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        return InventoryMapper.toDomain(
                inventoryJpaRepository.save(InventoryMapper.toEntity(inventory))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> findByMedicineId(Long medicineId) {
        return inventoryJpaRepository.findByMedicineId(medicineId)
                .map(InventoryMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public List<Inventory> findLowStock() {
        return inventoryJpaRepository.findAll()
                .stream()
                .filter(e -> e.getStockQuantity() <= e.getReorderLevel())
                .map(InventoryMapper::toDomain)
                .toList();
    }
}