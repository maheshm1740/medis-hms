package com.medis.hospital_system.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medis.hospital_system.application.command.RegisterDoctorCommand;
import com.medis.hospital_system.application.port.in.DoctorUseCase;
import com.medis.hospital_system.application.port.in.UserUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Doctor;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoctorUseCase doctorUseCase;

    @MockitoBean
    private UserUseCase userUseCase;

//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private Doctor validDoctor() {
        return new Doctor(1L, 1L, List.of("Cardiology"), "Cardiology", "KA-MED-001", 12);
    }

    @Test
    void shouldRegisterDoctorAndReturn201() throws Exception {
        when(doctorUseCase.registerDoctor(any(RegisterDoctorCommand.class))).thenReturn(validDoctor());

        mockMvc.perform(post("/api/v1/doctors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "specialization": ["Cardiology"],
                                  "department": "Cardiology",
                                  "licenseNumber": "KA-MED-001",
                                  "experienceYears": 12
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.department").value("Cardiology"))
                .andExpect(jsonPath("$.licenseNumber").value("KA-MED-001"))
                .andExpect(jsonPath("$.experiencedDoctor").value(true));
    }

    @Test
    void shouldReturn409WhenDoctorProfileAlreadyExists() throws Exception {
        when(doctorUseCase.registerDoctor(any(RegisterDoctorCommand.class)))
                .thenThrow(new DuplicateResourceException("A doctor profile already exists for user id: 1"));

        mockMvc.perform(post("/api/v1/doctors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1,
                                  "specialization": ["Cardiology"],
                                  "department": "Cardiology",
                                  "licenseNumber": "KA-MED-001",
                                  "experienceYears": 12
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A doctor profile already exists for user id: 1"));
    }

    @Test
    void shouldReturn400WhenRegisterRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/doctors/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": null,
                                  "specialization": [],
                                  "department": "",
                                  "licenseNumber": "",
                                  "experienceYears": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldGetDoctorByIdAndReturn200() throws Exception {
        when(doctorUseCase.getDoctorById(1L)).thenReturn(validDoctor());

        mockMvc.perform(get("/api/v1/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.licenseNumber").value("KA-MED-001"));
    }

    @Test
    void shouldReturn404WhenDoctorNotFoundById() throws Exception {
        when(doctorUseCase.getDoctorById(99L))
                .thenThrow(new ResourceNotFoundException("Doctor not found with id: 99"));

        mockMvc.perform(get("/api/v1/doctors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found with id: 99"));
    }

    @Test
    void shouldGetDoctorByUserIdAndReturn200() throws Exception {
        when(doctorUseCase.getDoctorByUserId(1L)).thenReturn(validDoctor());

        mockMvc.perform(get("/api/v1/doctors/by-user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldGetAllDoctorsAndReturn200() throws Exception {
        when(doctorUseCase.getAllDoctors()).thenReturn(List.of(validDoctor()));

        mockMvc.perform(get("/api/v1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetDoctorsBySpecializationAndReturn200() throws Exception {
        when(doctorUseCase.getDoctorsBySpecialization("Cardiology")).thenReturn(List.of(validDoctor()));

        mockMvc.perform(get("/api/v1/doctors/by-specialization")
                        .param("specialization", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].specialization[0]").value("Cardiology"));
    }

    @Test
    void shouldUpdateDepartmentAndReturn200() throws Exception {
        Doctor updated = new Doctor(1L, 1L, List.of("Cardiology"), "Neurology", "KA-MED-001", 12);
        when(doctorUseCase.updateDepartment(1L, "Neurology")).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/doctors/1/department")
                        .param("department", "Neurology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Neurology"));
    }

    @Test
    void shouldUpdateSpecializationAndReturn200() throws Exception {
        Doctor updated = new Doctor(1L, 1L, List.of("Neurology", "Psychiatry"), "Cardiology", "KA-MED-001", 12);
        when(doctorUseCase.updateSpecialization(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/doctors/1/specialization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                ["Neurology", "Psychiatry"]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization[0]").value("Neurology"))
                .andExpect(jsonPath("$.specialization[1]").value("Psychiatry"));
    }
}
