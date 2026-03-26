package com.medis.hospital_system.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medis.hospital_system.application.command.RegisterPatientCommand;
import com.medis.hospital_system.application.port.in.PatientUseCase;
import com.medis.hospital_system.application.port.in.UserUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.infrastructure.config.TestSecurityConfig;
import com.medis.hospital_system.infrastructure.exception.GlobalExceptionHandler;
import com.medis.hospital_system.infrastructure.security.JwtAuthenticationFilter;
import com.medis.hospital_system.infrastructure.security.JwtTokenProvider;
import com.medis.hospital_system.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientUseCase patientUseCase;

    @MockitoBean
    private UserUseCase userUseCase;
//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private Patient validPatient() {
        return new Patient(1L, 2L, "O+", "123 MG Road, Bangalore", "9988776655");
    }

    @Test
    void shouldRegisterPatientAndReturn201() throws Exception {
        when(patientUseCase.registerPatient(any(RegisterPatientCommand.class))).thenReturn(validPatient());

        mockMvc.perform(post("/api/v1/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "bloodGroup": "O+",
                                  "address": "123 MG Road, Bangalore",
                                  "emergencyContact": "9988776655"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.bloodGroup").value("O+"))
                .andExpect(jsonPath("$.address").value("123 MG Road, Bangalore"))
                .andExpect(jsonPath("$.emergencyContact").value("9988776655"));
    }

    @Test
    void shouldReturn409WhenPatientProfileAlreadyExists() throws Exception {
        when(patientUseCase.registerPatient(any(RegisterPatientCommand.class)))
                .thenThrow(new DuplicateResourceException("A patient profile already exists for user id: 2"));

        mockMvc.perform(post("/api/v1/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "bloodGroup": "O+",
                                  "address": "123 MG Road, Bangalore",
                                  "emergencyContact": "9988776655"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A patient profile already exists for user id: 2"));
    }

    @Test
    void shouldReturn400WhenRegisterRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": null,
                                  "bloodGroup": "INVALID",
                                  "address": "",
                                  "emergencyContact": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldGetPatientByIdAndReturn200() throws Exception {
        when(patientUseCase.getPatientById(1L)).thenReturn(validPatient());

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bloodGroup").value("O+"));
    }

    @Test
    void shouldReturn404WhenPatientNotFoundById() throws Exception {
        when(patientUseCase.getPatientById(99L))
                .thenThrow(new ResourceNotFoundException("Patient not found with id: 99"));

        mockMvc.perform(get("/api/v1/patients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 99"));
    }

    @Test
    void shouldGetPatientByUserIdAndReturn200() throws Exception {
        when(patientUseCase.getPatientByUserId(2L)).thenReturn(validPatient());

        mockMvc.perform(get("/api/v1/patients/by-user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void shouldReturn404WhenPatientNotFoundByUserId() throws Exception {
        when(patientUseCase.getPatientByUserId(99L))
                .thenThrow(new ResourceNotFoundException("Patient profile not found for user id: 99"));

        mockMvc.perform(get("/api/v1/patients/by-user/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient profile not found for user id: 99"));
    }

    @Test
    void shouldUpdateAddressAndReturn200() throws Exception {
        Patient updated = new Patient(1L, 2L, "O+", "456 Brigade Road, Bangalore", "9988776655");
        when(patientUseCase.updateAddress(1L, "456 Brigade Road, Bangalore")).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/patients/1/address")
                        .param("address", "456 Brigade Road, Bangalore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("456 Brigade Road, Bangalore"));
    }

    @Test
    void shouldUpdateEmergencyContactAndReturn200() throws Exception {
        Patient updated = new Patient(1L, 2L, "O+", "123 MG Road, Bangalore", "1122334455");
        when(patientUseCase.updateEmergencyContact(1L, "1122334455")).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/patients/1/emergency-contact")
                        .param("contact", "1122334455"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyContact").value("1122334455"));
    }

    @Test
    void shouldReturn404WhenUpdatingAddressOfNonExistentPatient() throws Exception {
        when(patientUseCase.updateAddress(99L, "New Address"))
                .thenThrow(new ResourceNotFoundException("Patient not found with id: 99"));

        mockMvc.perform(patch("/api/v1/patients/99/address")
                        .param("address", "New Address"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with id: 99"));
    }
}
