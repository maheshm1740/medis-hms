package com.medis.hospital_system.adapters.in.web.controller;

import com.medis.hospital_system.adapters.in.web.dto.request.AddMedicineRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.MedicineResponse;
import com.medis.hospital_system.application.command.AddMedicineCommand;
import com.medis.hospital_system.application.port.in.MedicineUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medicines")
@Tag(name = "Medicines", description = "Medicine catalogue management")
public class MedicineController {

    private final MedicineUseCase medicineUseCase;

    public MedicineController(MedicineUseCase medicineUseCase) {
        this.medicineUseCase = medicineUseCase;
    }

    @PostMapping
    @Operation(summary = "Add a new medicine")
    public ResponseEntity<MedicineResponse> add(@Valid @RequestBody AddMedicineRequest request) {
        AddMedicineCommand command = new AddMedicineCommand(
                request.name(),
                request.manufacturer(),
                request.price()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MedicineResponse.from(medicineUseCase.addMedicine(command)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID")
    public ResponseEntity<MedicineResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(MedicineResponse.from(medicineUseCase.getMedicineById(id)));
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get medicine by name")
    public ResponseEntity<MedicineResponse> getByName(@RequestParam String name) {
        return ResponseEntity.ok(MedicineResponse.from(medicineUseCase.getMedicineByName(name)));
    }

    @GetMapping
    @Operation(summary = "Get all medicines")
    public ResponseEntity<List<MedicineResponse>> getAll(
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(
                medicineUseCase.getAllMedicines(search)
                        .stream()
                        .map(MedicineResponse::from)
                        .toList()
        );
    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Update medicine price")
    public ResponseEntity<MedicineResponse> updatePrice(@PathVariable Long id,
                                                        @RequestParam BigDecimal price) {
        return ResponseEntity.ok(MedicineResponse.from(medicineUseCase.updatePrice(id, price)));
    }

    @PatchMapping("/{id}/manufacturer")
    @Operation(summary = "Update medicine manufacturer")
    public ResponseEntity<MedicineResponse> updateManufacturer(@PathVariable Long id,
                                                               @RequestParam String manufacturer) {
        return ResponseEntity.ok(MedicineResponse.from(medicineUseCase.updateManufacturer(id, manufacturer)));
    }
}