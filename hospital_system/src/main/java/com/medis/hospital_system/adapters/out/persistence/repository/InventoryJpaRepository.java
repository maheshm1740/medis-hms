package com.medis.hospital_system.adapters.out.persistence.repository;

import com.medis.hospital_system.adapters.out.persistence.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByMedicineId(Long medicineId);

    boolean existsByMedicineId(Long medicineId);

    List<InventoryEntity> findByStockQuantityLessThan(Integer quantity);

    void deleteByMedicineId(Long medicineId);
}