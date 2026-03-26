package com.medis.hospital_system.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medis.hospital_system.application.command.CreateInventoryCommand;
import com.medis.hospital_system.application.command.StockUpdateCommand;
import com.medis.hospital_system.application.port.in.InventoryUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.InsufficientStockException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Inventory;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryUseCase inventoryUseCase;

//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private Inventory validInventory() {
        return new Inventory(1L, 1L, 100, 20);
    }

    @Test
    void shouldCreateInventoryAndReturn201() throws Exception {
        when(inventoryUseCase.createInventory(any(CreateInventoryCommand.class)))
                .thenReturn(validInventory());

        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicineId": 1,
                                  "stockQuantity": 100,
                                  "reorderLevel": 20
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.medicineId").value(1))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andExpect(jsonPath("$.reorderLevel").value(20))
                .andExpect(jsonPath("$.lowStock").value(false));
    }

    @Test
    void shouldReturn409WhenInventoryAlreadyExistsForMedicine() throws Exception {
        when(inventoryUseCase.createInventory(any(CreateInventoryCommand.class)))
                .thenThrow(new DuplicateResourceException("Inventory already exists for medicine id: 1"));

        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicineId": 1,
                                  "stockQuantity": 100,
                                  "reorderLevel": 20
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Inventory already exists for medicine id: 1"));
    }

    @Test
    void shouldReturn400WhenCreateInventoryRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicineId": null,
                                  "stockQuantity": -1,
                                  "reorderLevel": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldGetInventoryByMedicineIdAndReturn200() throws Exception {
        when(inventoryUseCase.getInventoryByMedicineId(1L)).thenReturn(validInventory());

        mockMvc.perform(get("/api/v1/inventory/by-medicine/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicineId").value(1))
                .andExpect(jsonPath("$.stockQuantity").value(100));
    }

    @Test
    void shouldReturn404WhenInventoryNotFoundByMedicineId() throws Exception {
        when(inventoryUseCase.getInventoryByMedicineId(99L))
                .thenThrow(new ResourceNotFoundException("Inventory not found for medicine id: 99"));

        mockMvc.perform(get("/api/v1/inventory/by-medicine/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Inventory not found for medicine id: 99"));
    }

    @Test
    void shouldAddStockAndReturn200() throws Exception {
        Inventory updated = new Inventory(1L, 1L, 150, 20);
        when(inventoryUseCase.addStock(any(StockUpdateCommand.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/inventory/by-medicine/1/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 50
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(150));
    }

    @Test
    void shouldReturn400WhenAddStockQuantityIsInvalid() throws Exception {
        mockMvc.perform(patch("/api/v1/inventory/by-medicine/1/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldDeductStockAndReturn200() throws Exception {
        Inventory updated = new Inventory(1L, 1L, 70, 20);
        when(inventoryUseCase.deductStock(any(StockUpdateCommand.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/inventory/by-medicine/1/deduct-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 30
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(70));
    }

    @Test
    void shouldReturn422WhenDeductingMoreThanAvailableStock() throws Exception {
        when(inventoryUseCase.deductStock(any(StockUpdateCommand.class)))
                .thenThrow(new InsufficientStockException("Not enough stock available"));

        mockMvc.perform(patch("/api/v1/inventory/by-medicine/1/deduct-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 500
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Not enough stock available"));
    }

    @Test
    void shouldUpdateReorderLevelAndReturn200() throws Exception {
        Inventory updated = new Inventory(1L, 1L, 100, 30);
        when(inventoryUseCase.updateReorderLevel(1L, 30)).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/inventory/by-medicine/1/reorder-level")
                        .param("reorderLevel", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reorderLevel").value(30));
    }

    @Test
    void shouldCheckLowStockAndReturn200() throws Exception {
        when(inventoryUseCase.isLowStock(1L)).thenReturn(false);

        mockMvc.perform(get("/api/v1/inventory/by-medicine/1/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void shouldReturnTrueWhenStockIsLow() throws Exception {
        when(inventoryUseCase.isLowStock(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/inventory/by-medicine/1/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void shouldReturn404WhenCheckingLowStockForNonExistentInventory() throws Exception {
        when(inventoryUseCase.isLowStock(99L))
                .thenThrow(new ResourceNotFoundException("Inventory not found for medicine id: 99"));

        mockMvc.perform(get("/api/v1/inventory/by-medicine/99/low-stock"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Inventory not found for medicine id: 99"));
    }
}
