package com.medis.hospital_system.adapters.in.web.controller;

import com.medis.hospital_system.adapters.in.web.dto.request.RegisterDoctorRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.DoctorResponse;
import com.medis.hospital_system.application.command.RegisterDoctorCommand;
import com.medis.hospital_system.application.port.in.DoctorUseCase;
import com.medis.hospital_system.application.port.in.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctors", description = "Doctor registration and management")
public class DoctorController {

    private final DoctorUseCase doctorUseCase;
    private final UserUseCase userUseCase;

    public DoctorController(DoctorUseCase doctorUseCase, UserUseCase userUseCase) {
        this.doctorUseCase = doctorUseCase;
        this.userUseCase = userUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new doctor profile")
    public ResponseEntity<DoctorResponse> register(@Valid @RequestBody RegisterDoctorRequest request) {
        RegisterDoctorCommand command = new RegisterDoctorCommand(
                request.email(),
                request.specialization(),
                request.department(),
                request.licenseNumber(),
                request.experienceYears()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DoctorResponse.from(doctorUseCase.registerDoctor(command)));
    }

    @GetMapping("/me")
    @Operation(summary = "Get logged-in doctor's own profile")
    public ResponseEntity<DoctorResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userUseCase.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(DoctorResponse.from(doctorUseCase.getDoctorByUserId(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(DoctorResponse.from(doctorUseCase.getDoctorById(id)));
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get doctor profile by user ID")
    public ResponseEntity<DoctorResponse> getDoctorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(DoctorResponse.from(doctorUseCase.getDoctorByUserId(userId)));
    }

    @GetMapping
    @Operation(summary = "Get all doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorUseCase.getAllDoctors().stream()
                .map(DoctorResponse::from)
                .toList());
    }

    @GetMapping("/by-specialization")
    @Operation(summary = "Get doctors by specialization")
    public ResponseEntity<List<DoctorResponse>> getBySpecialization(@RequestParam String specialization) {
        return ResponseEntity.ok(doctorUseCase.getDoctorsBySpecialization(specialization).stream()
                .map(DoctorResponse::from)
                .toList());
    }

    @PatchMapping("/{id}/department")
    @Operation(summary = "Update doctor's department")
    public ResponseEntity<DoctorResponse> updateDepartment(@PathVariable Long id,
                                                           @RequestParam String department) {
        return ResponseEntity.ok(DoctorResponse.from(doctorUseCase.updateDepartment(id, department)));
    }

    @PatchMapping("/{id}/specialization")
    @Operation(summary = "Update doctor's specialization")
    public ResponseEntity<DoctorResponse> updateSpecialization(@PathVariable Long id,
                                                               @RequestBody List<String> specialization) {
        return ResponseEntity.ok(DoctorResponse.from(doctorUseCase.updateSpecialization(id, specialization)));
    }
}