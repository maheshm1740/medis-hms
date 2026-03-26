package com.medis.hospital_system.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MedicineTest {

    private Medicine validMedicine() {
        return new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("25.50"));
    }

    @Test
    void shouldCreateMedicineSuccessfully() {
        Medicine medicine = validMedicine();

        assertThat(medicine.getId()).isEqualTo(1L);
        assertThat(medicine.getName()).isEqualTo("Paracetamol");
        assertThat(medicine.getManufacturer()).isEqualTo("Sun Pharma");
        assertThat(medicine.getPrice()).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() ->
                new Medicine(1L, "", "Sun Pharma", new BigDecimal("25.50")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThatThrownBy(() ->
                new Medicine(1L, null, "Sun Pharma", new BigDecimal("25.50")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void shouldThrowWhenManufacturerIsBlank() {
        assertThatThrownBy(() ->
                new Medicine(1L, "Paracetamol", "", new BigDecimal("25.50")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Manufacturer");
    }

    @Test
    void shouldThrowWhenManufacturerIsNull() {
        assertThatThrownBy(() ->
                new Medicine(1L, "Paracetamol", null, new BigDecimal("25.50")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Manufacturer");
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        assertThatThrownBy(() ->
                new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price");
    }

    @Test
    void shouldThrowWhenPriceIsNull() {
        assertThatThrownBy(() ->
                new Medicine(1L, "Paracetamol", "Sun Pharma", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price");
    }

    @Test
    void shouldAllowZeroPrice() {
        assertThatNoException().isThrownBy(() ->
                new Medicine(1L, "Paracetamol", "Sun Pharma", BigDecimal.ZERO));
    }

    @Test
    void shouldUpdatePriceSuccessfully() {
        Medicine medicine = validMedicine();
        medicine.updatePrice(new BigDecimal("30.00"));
        assertThat(medicine.getPrice()).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    void shouldThrowWhenUpdatingPriceToNegative() {
        Medicine medicine = validMedicine();
        assertThatThrownBy(() -> medicine.updatePrice(new BigDecimal("-5.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid price");
    }

    @Test
    void shouldUpdateManufacturerSuccessfully() {
        Medicine medicine = validMedicine();
        medicine.updateManufacturer("Cipla");
        assertThat(medicine.getManufacturer()).isEqualTo("Cipla");
    }

    @Test
    void shouldThrowWhenUpdatingManufacturerToBlank() {
        Medicine medicine = validMedicine();
        assertThatThrownBy(() -> medicine.updateManufacturer(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Manufacturer");
    }

    @Test
    void shouldReturnTrueWhenPriceIsExpensive() {
        Medicine medicine = validMedicine();
        assertThat(medicine.isExpensive(new BigDecimal("20.00"))).isTrue();
        assertThat(medicine.isExpensive(new BigDecimal("30.00"))).isFalse();
    }
}