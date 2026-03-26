package com.medis.hospital_system.domain.model;

import com.medis.hospital_system.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InventoryTest {

    private Inventory validInventory() {
        return new Inventory(1L, 1L, 100, 20);
    }

    @Test
    void shouldCreateInventorySuccessfully() {
        Inventory inventory = validInventory();

        assertThat(inventory.getId()).isEqualTo(1L);
        assertThat(inventory.getMedicineId()).isEqualTo(1L);
        assertThat(inventory.getStockQuantity()).isEqualTo(100);
        assertThat(inventory.getReorderLevel()).isEqualTo(20);
    }

    @Test
    void shouldThrowWhenMedicineIdIsNull() {
        assertThatThrownBy(() ->
                new Inventory(1L, null, 100, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Medicine");
    }

    @Test
    void shouldThrowWhenStockQuantityIsNegative() {
        assertThatThrownBy(() ->
                new Inventory(1L, 1L, -1, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock quantity");
    }

    @Test
    void shouldThrowWhenReorderLevelIsNegative() {
        assertThatThrownBy(() ->
                new Inventory(1L, 1L, 100, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reorder level");
    }

    @Test
    void shouldAllowZeroStock() {
        assertThatNoException().isThrownBy(() ->
                new Inventory(1L, 1L, 0, 20));
    }

    @Test
    void shouldIncreaseStockSuccessfully() {
        Inventory inventory = validInventory();
        inventory.increaseStock(50);
        assertThat(inventory.getStockQuantity()).isEqualTo(150);
    }

    @Test
    void shouldThrowWhenIncreasingStockByZero() {
        Inventory inventory = validInventory();
        assertThatThrownBy(() -> inventory.increaseStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void shouldThrowWhenIncreasingStockByNegative() {
        Inventory inventory = validInventory();
        assertThatThrownBy(() -> inventory.increaseStock(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void shouldDecreaseStockSuccessfully() {
        Inventory inventory = validInventory();
        inventory.decreaseStock(30);
        assertThat(inventory.getStockQuantity()).isEqualTo(70);
    }

    @Test
    void shouldThrowWhenDecreasingStockByZero() {
        Inventory inventory = validInventory();
        assertThatThrownBy(() -> inventory.decreaseStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void shouldThrowWhenDecreasingMoreThanAvailableStock() {
        Inventory inventory = validInventory();
        assertThatThrownBy(() -> inventory.decreaseStock(200))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Not enough stock");
    }

    @Test
    void shouldUpdateReorderLevelSuccessfully() {
        Inventory inventory = validInventory();
        inventory.updateReorderLevel(30);
        assertThat(inventory.getReorderLevel()).isEqualTo(30);
    }

    @Test
    void shouldThrowWhenUpdatingReorderLevelToNegative() {
        Inventory inventory = validInventory();
        assertThatThrownBy(() -> inventory.updateReorderLevel(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reorder level");
    }

    @Test
    void shouldReturnTrueWhenStockIsLow() {
        Inventory inventory = new Inventory(1L, 1L, 20, 20);
        assertThat(inventory.isLowStock()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStockIsNotLow() {
        Inventory inventory = validInventory();
        assertThat(inventory.isLowStock()).isFalse();
    }

    @Test
    void shouldReturnTrueWhenStockIsAvailable() {
        Inventory inventory = validInventory();
        assertThat(inventory.hasStock()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStockIsEmpty() {
        Inventory inventory = new Inventory(1L, 1L, 0, 20);
        assertThat(inventory.hasStock()).isFalse();
    }
}
