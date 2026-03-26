package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.CreateInventoryCommand;
import com.medis.hospital_system.application.command.StockUpdateCommand;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.InsufficientStockException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Inventory;
import com.medis.hospital_system.domain.model.Medicine;
import com.medis.hospital_system.domain.repository.InventoryRepository;
import com.medis.hospital_system.domain.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Medicine validMedicine() {
        return new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("25.50"));
    }

    private Inventory validInventory() {
        return new Inventory(1L, 1L, 100, 20);
    }

    private CreateInventoryCommand createCommand() {
        return new CreateInventoryCommand(1L, 100, 20);
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        CreateInventoryCommand command = createCommand();

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(validMedicine()));
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(validInventory());

        Inventory result = inventoryService.createInventory(command);

        assertThat(result.getMedicineId()).isEqualTo(1L);
        assertThat(result.getStockQuantity()).isEqualTo(100);
        assertThat(result.getReorderLevel()).isEqualTo(20);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldThrowWhenMedicineNotFoundDuringInventoryCreation() {
        CreateInventoryCommand command = createCommand();

        when(medicineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.createInventory(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medicine");

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenInventoryAlreadyExistsForMedicine() {
        CreateInventoryCommand command = createCommand();

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(validMedicine()));
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(validInventory()));

        assertThatThrownBy(() -> inventoryService.createInventory(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Inventory");

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldGetInventoryByMedicineIdSuccessfully() {
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(validInventory()));

        Inventory result = inventoryService.getInventoryByMedicineId(1L);

        assertThat(result.getMedicineId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenInventoryNotFoundByMedicineId() {
        when(inventoryRepository.findByMedicineId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getInventoryByMedicineId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Inventory");
    }

    @Test
    void shouldAddStockSuccessfully() {
        Inventory inventory = validInventory();
        StockUpdateCommand command = new StockUpdateCommand(1L, 50);

        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory result = inventoryService.addStock(command);

        assertThat(result.getStockQuantity()).isEqualTo(150);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldThrowWhenAddingStockForNonExistentInventory() {
        StockUpdateCommand command = new StockUpdateCommand(99L, 50);

        when(inventoryRepository.findByMedicineId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.addStock(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Inventory");
    }

    @Test
    void shouldDeductStockSuccessfully() {
        Inventory inventory = validInventory();
        StockUpdateCommand command = new StockUpdateCommand(1L, 30);

        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory result = inventoryService.deductStock(command);

        assertThat(result.getStockQuantity()).isEqualTo(70);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldThrowWhenDeductingMoreThanAvailableStock() {
        Inventory inventory = validInventory();
        StockUpdateCommand command = new StockUpdateCommand(1L, 200);

        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> inventoryService.deductStock(command))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Not enough stock");
    }

    @Test
    void shouldUpdateReorderLevelSuccessfully() {
        Inventory inventory = validInventory();

        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory result = inventoryService.updateReorderLevel(1L, 30);

        assertThat(result.getReorderLevel()).isEqualTo(30);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldReturnTrueWhenStockIsLow() {
        Inventory inventory = new Inventory(1L, 1L, 20, 20);

        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(inventory));

        assertThat(inventoryService.isLowStock(1L)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStockIsNotLow() {
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(validInventory()));

        assertThat(inventoryService.isLowStock(1L)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenStockIsAvailable() {
        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(validInventory()));

        assertThat(inventoryService.hasStock(1L)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStockIsEmpty() {
        Inventory emptyInventory = new Inventory(1L, 1L, 0, 20);

        when(inventoryRepository.findByMedicineId(1L)).thenReturn(Optional.of(emptyInventory));

        assertThat(inventoryService.hasStock(1L)).isFalse();
    }
}
