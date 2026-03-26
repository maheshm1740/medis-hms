package com.medis.hospital_system.adapters.in.web.controller;

import com.medis.hospital_system.adapters.in.web.dto.request.CreateStaffRequest;
import com.medis.hospital_system.adapters.in.web.dto.request.RegisterUserRequest;
import com.medis.hospital_system.adapters.in.web.dto.request.UpdatePasswordRequest;
import com.medis.hospital_system.adapters.in.web.dto.request.UpdatePhoneRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.UserResponse;
import com.medis.hospital_system.application.command.RegisterUserCommand;
import com.medis.hospital_system.application.command.UpdatePasswordCommand;
import com.medis.hospital_system.application.command.UpdatePhoneCommand;
import com.medis.hospital_system.application.port.in.UserUseCase;
import com.medis.hospital_system.domain.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User registration and management")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Patient self-registration")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.name(),
                request.email(),
                request.password(),
                request.phone(),
                Role.PATIENT,
                true
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(userUseCase.registerUser(command)));
    }

    // Admin only — create staff/admin/doctor
    @PostMapping("/admin/create")
    @Operation(summary = "Admin creates staff account")
    public ResponseEntity<UserResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.name(),
                request.email(),
                request.password(),
                request.phone(),
                request.role(),
                false   // admin-created → must change password on first login
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(userUseCase.registerUser(command)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(userUseCase.getUserById(id)));
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get user by email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(UserResponse.from(userUseCase.getUserByEmail(email)));
    }

    @PatchMapping("/{id}/phone")
    @Operation(summary = "Update user phone number")
    public ResponseEntity<UserResponse> updatePhone(@PathVariable Long id,
                                                    @Valid @RequestBody UpdatePhoneRequest request) {
        UpdatePhoneCommand command = new UpdatePhoneCommand(id, request.phone());
        return ResponseEntity.ok(UserResponse.from(userUseCase.updatePhone(command)));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Update user password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id,
                                               @Valid @RequestBody UpdatePasswordRequest request) {
        UpdatePasswordCommand command = new UpdatePasswordCommand(
                id,
                request.oldPassword(),
                request.newPassword()
        );
        userUseCase.updatePassword(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                userUseCase.getAllUsers(search, page, size)
                        .map(UserResponse::from)
        );
    }
}