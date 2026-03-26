package com.medis.hospital_system.adapters.in.web.controller;

import com.medis.hospital_system.adapters.in.web.dto.request.RegisterPatientRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.PageResponse;
import com.medis.hospital_system.adapters.in.web.dto.response.PatientResponse;
import com.medis.hospital_system.application.command.RegisterPatientCommand;
import com.medis.hospital_system.application.port.in.PatientUseCase;
import com.medis.hospital_system.application.port.in.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Patient registration and management")
public class PatientController {

    private final PatientUseCase patientUseCase;
    private final UserUseCase userUseCase;

    public PatientController(PatientUseCase patientUseCase, UserUseCase userUseCase) {
        this.patientUseCase = patientUseCase;
        this.userUseCase = userUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new patient profile")
    public ResponseEntity<PatientResponse> register(
            @Valid @RequestBody RegisterPatientRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PatientResponse.from(
                        patientUseCase.registerPatient(
                                new RegisterPatientCommand(
                                        request.email(),
                                        request.bloodGroup(),
                                        request.address(),
                                        request.emergencyContact()
                                )
                        )
                ));
    }

    @GetMapping("/me")
    @Operation(summary = "Get logged-in patient's own profile")
    public ResponseEntity<PatientResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userUseCase.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(
                PatientResponse.from(patientUseCase.getPatientByUserId(userId))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<PatientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(PatientResponse.from(patientUseCase.getPatientById(id)));
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get patient profile by user ID")
    public ResponseEntity<PatientResponse> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(PatientResponse.from(patientUseCase.getPatientByUserId(userId)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PatientResponse>> getAllPatients(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PatientResponse> result = patientUseCase.getAllPatients(name, pageable)
                .map(PatientResponse::from);

        return ResponseEntity.ok(
                new PageResponse<>(
                        result.getContent(),
                        result.getNumber(),
                        result.getSize(),
                        result.getTotalElements(),
                        result.getTotalPages()
                )
        );
    }
    @PatchMapping("/{id}/address")
    @Operation(summary = "Update patient address")
    public ResponseEntity<PatientResponse> updateAddress(
            @PathVariable Long id,
            @RequestParam String address
    ) {
        return ResponseEntity.ok(
                PatientResponse.from(patientUseCase.updateAddress(id, address))
        );
    }

    @PatchMapping("/{id}/emergency-contact")
    @Operation(summary = "Update patient emergency contact")
    public ResponseEntity<PatientResponse> updateEmergencyContact(
            @PathVariable Long id,
            @RequestParam String contact
    ) {
        return ResponseEntity.ok(
                PatientResponse.from(patientUseCase.updateEmergencyContact(id, contact))
        );
    }
}