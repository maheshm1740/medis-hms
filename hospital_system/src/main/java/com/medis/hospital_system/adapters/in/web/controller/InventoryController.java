package com.medis.hospital_system.adapters.in.web.controller;

import com.medis.hospital_system.adapters.in.web.dto.request.CreateInventoryRequest;
import com.medis.hospital_system.adapters.in.web.dto.request.StockUpdateRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.InventoryResponse;
import com.medis.hospital_system.application.command.CreateInventoryCommand;
import com.medis.hospital_system.application.command.StockUpdateCommand;
import com.medis.hospital_system.application.port.in.InventoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Medicine inventory and stock management")
public class InventoryController {

    private final InventoryUseCase inventoryUseCase;

    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    @PostMapping
    @Operation(summary = "Create inventory entry for a medicine")
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody CreateInventoryRequest request) {
        CreateInventoryCommand command = new CreateInventoryCommand(
                request.medicineId(),
                request.stockQuantity(),
                request.reorderLevel()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponse.from(inventoryUseCase.createInventory(command)));
    }

    @GetMapping("/by-medicine/{medicineId}")
    @Operation(summary = "Get inventory for a specific medicine")
    public ResponseEntity<InventoryResponse> getByMedicine(@PathVariable Long medicineId) {
        return ResponseEntity.ok(InventoryResponse.from(inventoryUseCase.getInventoryByMedicineId(medicineId)));
    }

    @PatchMapping("/by-medicine/{medicineId}/add-stock")
    @Operation(summary = "Add stock for a medicine")
    public ResponseEntity<InventoryResponse> addStock(@PathVariable Long medicineId,
                                                      @Valid @RequestBody StockUpdateRequest request) {
        StockUpdateCommand command = new StockUpdateCommand(medicineId, request.quantity());
        return ResponseEntity.ok(InventoryResponse.from(inventoryUseCase.addStock(command)));
    }

    @PatchMapping("/by-medicine/{medicineId}/deduct-stock")
    @Operation(summary = "Deduct stock for a medicine")
    public ResponseEntity<InventoryResponse> deductStock(@PathVariable Long medicineId,
                                                         @Valid @RequestBody StockUpdateRequest request) {
        StockUpdateCommand command = new StockUpdateCommand(medicineId, request.quantity());
        return ResponseEntity.ok(InventoryResponse.from(inventoryUseCase.deductStock(command)));
    }

    @PatchMapping("/by-medicine/{medicineId}/reorder-level")
    @Operation(summary = "Update reorder level for a medicine")
    public ResponseEntity<InventoryResponse> updateReorderLevel(@PathVariable Long medicineId,
                                                                @RequestParam int reorderLevel) {
        return ResponseEntity.ok(InventoryResponse.from(inventoryUseCase.updateReorderLevel(medicineId, reorderLevel)));
    }

    @GetMapping("/by-medicine/{medicineId}/low-stock")
    @Operation(summary = "Check if a medicine is low on stock")
    public ResponseEntity<Boolean> isLowStock(@PathVariable Long medicineId) {
        return ResponseEntity.ok(inventoryUseCase.isLowStock(medicineId));
    }
}