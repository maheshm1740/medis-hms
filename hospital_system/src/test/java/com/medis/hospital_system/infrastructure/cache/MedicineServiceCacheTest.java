// ============================================================
// FILE: MedicineServiceCacheTest.java
// ============================================================
package com.medis.hospital_system.infrastructure.cache;

import com.medis.hospital_system.application.command.AddMedicineCommand;
import com.medis.hospital_system.application.service.MedicineService;
import com.medis.hospital_system.domain.model.Medicine;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
class MedicineServiceCacheTest {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private MedicineRepository medicineRepository;

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
    void getMedicineById_shouldCacheOnFirstCall() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(sampleMedicine()));

        medicineService.getMedicineById(1L);
        medicineService.getMedicineById(1L);
        medicineService.getMedicineById(1L);

        verify(medicineRepository, times(1)).findById(1L);
    }

    @Test
    void getMedicineByName_shouldCacheOnFirstCall() {
        when(medicineRepository.findByName("Paracetamol")).thenReturn(Optional.of(sampleMedicine()));

        medicineService.getMedicineByName("Paracetamol");
        medicineService.getMedicineByName("Paracetamol");

        verify(medicineRepository, times(1)).findByName("Paracetamol");
    }

    // ✅ FIXED: pass null for "get all"
    @Test
    void getAllMedicines_shouldCacheOnFirstCall() {
        when(medicineRepository.findAll()).thenReturn(List.of(sampleMedicine()));

        medicineService.getAllMedicines(null);
        medicineService.getAllMedicines(null);

        verify(medicineRepository, times(1)).findAll();
    }

    // ✅ NEW: search caching test (important)
    @Test
    void searchMedicines_shouldCacheSeparately() {
        when(medicineRepository.searchMedicines("para"))
                .thenReturn(List.of(sampleMedicine()));

        medicineService.getAllMedicines("para");
        medicineService.getAllMedicines("para");

        verify(medicineRepository, times(1)).searchMedicines("para");
    }

    @Test
    void addMedicine_shouldEvictAllMedicinesCache() {
        when(medicineRepository.findAll()).thenReturn(List.of(sampleMedicine()));
        when(medicineRepository.existsByName("Ibuprofen")).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(sampleMedicine());

        medicineService.getAllMedicines(null);
        verify(medicineRepository, times(1)).findAll();

        medicineService.addMedicine(
                new AddMedicineCommand("Ibuprofen", "Cipla", new BigDecimal("15.00"))
        );

        medicineService.getAllMedicines(null);
        verify(medicineRepository, times(2)).findAll();
    }

    @Test
    void updatePrice_shouldEvictListCacheAndRepopulateSingleEntry() {
        Medicine medicine = sampleMedicine();
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.findAll()).thenReturn(List.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        medicineService.getAllMedicines(null);
        verify(medicineRepository, times(1)).findAll();

        medicineService.updatePrice(1L, new BigDecimal("30.00"));

        medicineService.getAllMedicines(null);
        verify(medicineRepository, times(2)).findAll();

        medicineService.getMedicineById(1L);
        verify(medicineRepository, times(1)).findById(1L);
    }

    @Test
    void updateManufacturer_shouldEvictListCacheAndRepopulateSingleEntry() {
        Medicine medicine = sampleMedicine();
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.findAll()).thenReturn(List.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        medicineService.getAllMedicines(null);
        medicineService.updateManufacturer(1L, "Cipla");
        medicineService.getAllMedicines(null);

        verify(medicineRepository, times(2)).findAll();
    }

    @Test
    void getAllMedicines_cacheShouldContainEntryAfterFirstCall() {
        when(medicineRepository.findAll()).thenReturn(List.of(sampleMedicine()));

        medicineService.getAllMedicines(null);

        var cache = cacheManager.getCache(CacheNames.MEDICINES_ALL);
        assertThat(cache).isNotNull();
        assertThat(cache.get("all")).isNotNull();
    }

    @Test
    void getMedicineById_cacheShouldContainEntryAfterFirstCall() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(sampleMedicine()));

        medicineService.getMedicineById(1L);

        var cache = cacheManager.getCache(CacheNames.MEDICINES);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();
    }
}