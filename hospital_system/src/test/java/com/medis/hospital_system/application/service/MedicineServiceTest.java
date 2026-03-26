package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.AddMedicineCommand;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Medicine;
import com.medis.hospital_system.domain.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    private Medicine validMedicine() {
        return new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("25.50"));
    }

    private AddMedicineCommand addCommand() {
        return new AddMedicineCommand("Paracetamol", "Sun Pharma", new BigDecimal("25.50"));
    }

    @Test
    void shouldAddMedicineSuccessfully() {
        AddMedicineCommand command = addCommand();

        when(medicineRepository.existsByName(command.name())).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(validMedicine());

        Medicine result = medicineService.addMedicine(command);

        assertThat(result.getName()).isEqualTo("Paracetamol");
        assertThat(result.getManufacturer()).isEqualTo("Sun Pharma");
        verify(medicineRepository).save(any(Medicine.class));
    }

    @Test
    void shouldThrowWhenMedicineNameAlreadyExists() {
        AddMedicineCommand command = addCommand();

        when(medicineRepository.existsByName(command.name())).thenReturn(true);

        assertThatThrownBy(() -> medicineService.addMedicine(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("medicine");

        verify(medicineRepository, never()).save(any());
    }

    @Test
    void shouldGetMedicineByIdSuccessfully() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(validMedicine()));

        Medicine result = medicineService.getMedicineById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenMedicineNotFoundById() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.getMedicineById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medicine");
    }

    @Test
    void shouldGetMedicineByNameSuccessfully() {
        when(medicineRepository.findByName("Paracetamol")).thenReturn(Optional.of(validMedicine()));

        Medicine result = medicineService.getMedicineByName("Paracetamol");

        assertThat(result.getName()).isEqualTo("Paracetamol");
    }

    @Test
    void shouldThrowWhenMedicineNotFoundByName() {
        when(medicineRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.getMedicineByName("Unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medicine");
    }

    @Test
    void shouldGetAllMedicinesSuccessfully() {

        when(medicineRepository.findAll())
                .thenReturn(List.of(validMedicine()));

        List<Medicine> result = medicineService.getAllMedicines(null);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnAllWhenSearchIsBlank() {

        when(medicineRepository.findAll())
                .thenReturn(List.of(validMedicine()));

        List<Medicine> result = medicineService.getAllMedicines("");

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldSearchMedicinesWhenSearchParamProvided() {

        when(medicineRepository.searchMedicines("para"))
                .thenReturn(List.of(validMedicine()));

        List<Medicine> result = medicineService.getAllMedicines("para");

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldUpdatePriceSuccessfully() {
        Medicine medicine = validMedicine();

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Medicine result = medicineService.updatePrice(1L, new BigDecimal("30.00"));

        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("30.00"));
        verify(medicineRepository).save(medicine);
    }

    @Test
    void shouldThrowWhenUpdatingPriceOfNonExistentMedicine() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.updatePrice(99L, new BigDecimal("30.00")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medicine");
    }

    @Test
    void shouldUpdateManufacturerSuccessfully() {
        Medicine medicine = validMedicine();

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Medicine result = medicineService.updateManufacturer(1L, "Cipla");

        assertThat(result.getManufacturer()).isEqualTo("Cipla");
        verify(medicineRepository).save(medicine);
    }

    @Test
    void shouldThrowWhenUpdatingManufacturerOfNonExistentMedicine() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicineService.updateManufacturer(99L, "Cipla"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Medicine");
    }
}