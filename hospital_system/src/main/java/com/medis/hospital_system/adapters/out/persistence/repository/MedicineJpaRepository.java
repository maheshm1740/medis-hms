package com.medis.hospital_system.adapters.out.persistence.repository;

import com.medis.hospital_system.adapters.out.persistence.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineJpaRepository extends JpaRepository<MedicineEntity, Long> {

    Optional<MedicineEntity> findByName(String name);

    boolean existsByName(String name);

    @Query("""
    SELECT m FROM MedicineEntity m
    WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    List<MedicineEntity> searchMedicines(@Param("search") String search);

    List<MedicineEntity> findByNameContainingIgnoreCase(String name);

    List<MedicineEntity> findByManufacturerContainingIgnoreCase(String manufacturer);

    // Removed: findByStockQuantityLessThan — stockQuantity belongs to InventoryEntity, not MedicineEntity
    // Removed: findByExpiryDateBefore — expiryDate field does not exist on MedicineEntity
}