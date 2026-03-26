package com.medis.hospital_system.infrastructure.cache;

import com.medis.hospital_system.application.command.CreateInventoryCommand;
import com.medis.hospital_system.application.command.StockUpdateCommand;
import com.medis.hospital_system.application.service.InventoryService;
import com.medis.hospital_system.domain.model.Inventory;
import com.medis.hospital_system.domain.model.Medicine;
import com.medis.hospital_system.domain.repository.InventoryRepository;
import com.medis.hospital_system.domain.repository.MedicineRepository;
import com.medis.hospital_system.infrastructure.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceCacheTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private InventoryRepository inventoryRepository;

    @MockitoBean
    private MedicineRepository medicineRepository;

    private Inventory sampleInventory() {
        return new Inventory(1L, 1L, 100, 20);
    }

    private Medicine sampleMedicine() {
        return new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("25.50"));
    }

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    @Test
    void getInventoryByMedicineId_shouldCacheOnFirstCall() {
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(sampleInventory()));

        inventoryService.getInventoryByMedicineId(1L);
        inventoryService.getInventoryByMedicineId(1L);
        inventoryService.getInventoryByMedicineId(1L);

        verify(inventoryRepository, times(1)).findByMedicineId(1L);
    }

    @Test
    void addStock_shouldUpdateCacheWithNewStockLevel() {
        Inventory inventory = sampleInventory();
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(
                new Inventory(1L, 1L, 150, 20)
        );

        inventoryService.getInventoryByMedicineId(1L);
        inventoryService.addStock(new StockUpdateCommand(1L, 50));
        inventoryService.getInventoryByMedicineId(1L);

        // 1 for prime + 1 inside addStock (self-call bypasses cache proxy)
        verify(inventoryRepository, times(2)).findByMedicineId(1L);
    }

    @Test
    void deductStock_shouldUpdateCacheWithReducedStockLevel() {
        Inventory inventory = sampleInventory();
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(
                new Inventory(1L, 1L, 70, 20)
        );

        inventoryService.getInventoryByMedicineId(1L);
        inventoryService.deductStock(new StockUpdateCommand(1L, 30));
        inventoryService.getInventoryByMedicineId(1L);

        verify(inventoryRepository, times(2)).findByMedicineId(1L);
    }

    @Test
    void updateReorderLevel_shouldUpdateCache() {
        Inventory inventory = sampleInventory();
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(
                new Inventory(1L, 1L, 100, 30)
        );

        inventoryService.getInventoryByMedicineId(1L);
        inventoryService.updateReorderLevel(1L, 30);
        inventoryService.getInventoryByMedicineId(1L);

        verify(inventoryRepository, times(2)).findByMedicineId(1L);
    }

    @Test
    void isLowStock_callsRepository_dueToSelfInvocation() {
        Inventory lowInventory = new Inventory(1L, 1L, 20, 20);
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(lowInventory));

        boolean result = inventoryService.isLowStock(1L);

        assertThat(result).isTrue();
        verify(inventoryRepository, times(1)).findByMedicineId(1L);
    }

    @Test
    void hasStock_callsRepository_dueToSelfInvocation() {
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(sampleInventory()));

        boolean result = inventoryService.hasStock(1L);

        assertThat(result).isTrue();
        verify(inventoryRepository, times(1)).findByMedicineId(1L);
    }

    @Test
    void createInventory_shouldEvictStaleEntry() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(sampleMedicine()));
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory());

        inventoryService.createInventory(new CreateInventoryCommand(1L, 100, 20));

        var cache = cacheManager.getCache(CacheNames.INVENTORY);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNull();
    }

    @Test
    void getInventoryByMedicineId_cacheShouldContainEntryAfterFirstCall() {
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(sampleInventory()));

        inventoryService.getInventoryByMedicineId(1L);

        var cache = cacheManager.getCache(CacheNames.INVENTORY);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();
    }
}
