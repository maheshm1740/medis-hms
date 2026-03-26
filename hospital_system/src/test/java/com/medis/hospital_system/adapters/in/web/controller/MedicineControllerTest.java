package com.medis.hospital_system.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medis.hospital_system.application.command.AddMedicineCommand;
import com.medis.hospital_system.application.port.in.MedicineUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Medicine;
import com.medis.hospital_system.infrastructure.config.TestSecurityConfig;
import com.medis.hospital_system.infrastructure.exception.GlobalExceptionHandler;
import com.medis.hospital_system.infrastructure.security.JwtAuthenticationFilter;
import com.medis.hospital_system.infrastructure.security.JwtTokenProvider;
import com.medis.hospital_system.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicineController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class MedicineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedicineUseCase medicineUseCase;

//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private Medicine validMedicine() {
        return new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("25.50"));
    }

    @Test
    void shouldAddMedicineAndReturn201() throws Exception {
        when(medicineUseCase.addMedicine(any(AddMedicineCommand.class))).thenReturn(validMedicine());

        mockMvc.perform(post("/api/v1/medicines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Paracetamol",
                                  "manufacturer": "Sun Pharma",
                                  "price": 25.50
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.manufacturer").value("Sun Pharma"))
                .andExpect(jsonPath("$.price").value(25.50));
    }

    @Test
    void shouldReturn409WhenMedicineNameAlreadyExists() throws Exception {
        when(medicineUseCase.addMedicine(any(AddMedicineCommand.class)))
                .thenThrow(new DuplicateResourceException("A medicine with this name already exists: Paracetamol"));

        mockMvc.perform(post("/api/v1/medicines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Paracetamol",
                                  "manufacturer": "Sun Pharma",
                                  "price": 25.50
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A medicine with this name already exists: Paracetamol"));
    }

    @Test
    void shouldReturn400WhenAddMedicineRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/medicines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "manufacturer": "",
                                  "price": -1.00
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldGetMedicineByIdAndReturn200() throws Exception {
        when(medicineUseCase.getMedicineById(1L)).thenReturn(validMedicine());

        mockMvc.perform(get("/api/v1/medicines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Paracetamol"));
    }

    @Test
    void shouldReturn404WhenMedicineNotFoundById() throws Exception {
        when(medicineUseCase.getMedicineById(99L))
                .thenThrow(new ResourceNotFoundException("Medicine not found with id: 99"));

        mockMvc.perform(get("/api/v1/medicines/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Medicine not found with id: 99"));
    }

    @Test
    void shouldGetMedicineByNameAndReturn200() throws Exception {
        when(medicineUseCase.getMedicineByName("Paracetamol")).thenReturn(validMedicine());

        mockMvc.perform(get("/api/v1/medicines/by-name")
                        .param("name", "Paracetamol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"));
    }

    @Test
    void shouldReturn404WhenMedicineNotFoundByName() throws Exception {
        when(medicineUseCase.getMedicineByName("Unknown"))
                .thenThrow(new ResourceNotFoundException("Medicine not found with name: Unknown"));

        mockMvc.perform(get("/api/v1/medicines/by-name")
                        .param("name", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Medicine not found with name: Unknown"));
    }


    @Test
    void shouldGetAllMedicinesAndReturn200() throws Exception {

        when(medicineUseCase.getAllMedicines(null))
                .thenReturn(List.of(validMedicine()));

        mockMvc.perform(get("/api/v1/medicines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Paracetamol"));
    }

    @Test
    void shouldUpdatePriceAndReturn200() throws Exception {
        Medicine updated = new Medicine(1L, "Paracetamol", "Sun Pharma", new BigDecimal("30.00"));
        when(medicineUseCase.updatePrice(1L, new BigDecimal("30.00"))).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/medicines/1/price")
                        .param("price", "30.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(30.00));
    }

    @Test
    void shouldReturn404WhenUpdatingPriceOfNonExistentMedicine() throws Exception {
        when(medicineUseCase.updatePrice(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Medicine not found with id: 99"));

        mockMvc.perform(patch("/api/v1/medicines/99/price")
                        .param("price", "30.00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Medicine not found with id: 99"));
    }

    @Test
    void shouldUpdateManufacturerAndReturn200() throws Exception {
        Medicine updated = new Medicine(1L, "Paracetamol", "Cipla", new BigDecimal("25.50"));
        when(medicineUseCase.updateManufacturer(1L, "Cipla")).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/medicines/1/manufacturer")
                        .param("manufacturer", "Cipla"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manufacturer").value("Cipla"));
    }

    @Test
    void shouldReturn404WhenUpdatingManufacturerOfNonExistentMedicine() throws Exception {
        when(medicineUseCase.updateManufacturer(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Medicine not found with id: 99"));

        mockMvc.perform(patch("/api/v1/medicines/99/manufacturer")
                        .param("manufacturer", "Cipla"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Medicine not found with id: 99"));
    }
}
