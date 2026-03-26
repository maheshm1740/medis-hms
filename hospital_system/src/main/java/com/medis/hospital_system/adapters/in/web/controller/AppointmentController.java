package com.medis.hospital_system.adapters.in.web.controller;

import com.medis.hospital_system.adapters.in.web.dto.request.BookAppointmentRequest;
import com.medis.hospital_system.adapters.in.web.dto.request.RescheduleAppointmentRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.AppointmentResponse;
import com.medis.hospital_system.application.command.BookAppointmentCommand;
import com.medis.hospital_system.application.command.RescheduleAppointmentCommand;
import com.medis.hospital_system.application.port.in.AppointmentUseCase;
import com.medis.hospital_system.application.port.in.DoctorUseCase;
import com.medis.hospital_system.application.port.in.PatientUseCase;
import com.medis.hospital_system.application.port.in.UserUseCase;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Appointment booking and management")
public class AppointmentController {

    private final AppointmentUseCase appointmentUseCase;
    private final UserUseCase userUseCase;
    private final DoctorUseCase doctorUseCase;
    private final PatientUseCase patientUseCase;

    public AppointmentController(AppointmentUseCase appointmentUseCase, UserUseCase userUseCase
                                    ,DoctorUseCase doctorUseCase, PatientUseCase patientUseCase) {
        this.appointmentUseCase = appointmentUseCase;
        this.userUseCase = userUseCase;
        this.doctorUseCase = doctorUseCase;
        this.patientUseCase = patientUseCase;
    }

    @PostMapping
    @Operation(summary = "Book a new appointment")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookAppointmentRequest request) {
        BookAppointmentCommand command = new BookAppointmentCommand(
                request.patientId(),
                request.doctorId(),
                request.appointmentTime(),
                request.notes()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppointmentResponse.from(appointmentUseCase.bookAppointment(command)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication auth) {

        String email = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        Long userId = userUseCase.getUserByEmail(email).getId();

        List<AppointmentResponse> response;

        if (role.equals("ROLE_DOCTOR")) {
            // ← Use the non-cached direct lookup to avoid Redis deserialization crash
            Long doctorId = doctorUseCase.getDoctorByUserIdDirect(userId).id();
            response = appointmentUseCase.getAppointmentsByDoctor(doctorId)
                    .stream().map(AppointmentResponse::from).toList();

        } else if (role.equals("ROLE_PATIENT")) {
            Long patientId = patientUseCase.getPatientByUserIdDirect(userId).id();
            response = appointmentUseCase.getAppointmentsByPatient(patientId)
                    .stream().map(AppointmentResponse::from).toList();

        } else {
            response = appointmentUseCase.getAllAppointments()
                    .stream().map(AppointmentResponse::from).toList();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-status")
    @Operation(summary = "Get appointments by status")
    public ResponseEntity<List<AppointmentResponse>> getByStatus(@RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentUseCase.getAppointmentsByStatus(status)
                .stream()
                .map(AppointmentResponse::from)
                .toList());
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark appointment as completed")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(AppointmentResponse.from(appointmentUseCase.completeAppointment(id)));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(AppointmentResponse.from(appointmentUseCase.cancelAppointment(id)));
    }

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment")
    public ResponseEntity<AppointmentResponse> reschedule(@PathVariable Long id,
                                                          @Valid @RequestBody RescheduleAppointmentRequest request) {
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(
                id,
                request.newAppointmentTime()
        );
        return ResponseEntity.ok(AppointmentResponse.from(
                appointmentUseCase.rescheduleAppointment(command)));
    }
}