package com.medis.hospital_system.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medis.hospital_system.application.command.RegisterUserCommand;
import com.medis.hospital_system.application.command.UpdatePasswordCommand;
import com.medis.hospital_system.application.command.UpdatePhoneCommand;
import com.medis.hospital_system.application.port.in.UserUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

//    @MockitoBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Required: SecurityConfig depends on UserDetailsServiceImpl;
    // without this @MockitoBean the WebMvcTest context fails to load.
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private User validPatient() {
        return new User(1L, "John Doe", "john@example.com", "encodedPassword", "9999999999", Role.PATIENT, true);
    }

    private User validDoctor() {
        return new User(2L, "Dr. Smith", "smith@example.com", "encodedPassword", "8888888888", Role.DOCTOR, false);
    }

    // ─── Register (public) ────────────────────────────────────────────────────

    @Test
    void shouldRegisterPatientAndReturn201() throws Exception {
        when(userUseCase.registerUser(any(RegisterUserCommand.class))).thenReturn(validPatient());

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Doe",
                                  "email": "john@example.com",
                                  "password": "password123",
                                  "phone": "9999999999"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andExpect(jsonPath("$.passwordChanged").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(userUseCase.registerUser(any(RegisterUserCommand.class)))
                .thenThrow(new DuplicateResourceException("A user with this email already exists"));

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Doe",
                                  "email": "john@example.com",
                                  "password": "password123",
                                  "phone": "9999999999"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this email already exists"));
    }

    @Test
    void shouldReturn400WhenRegisterRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "invalid-email",
                                  "password": "123",
                                  "phone": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // ─── Admin create staff ───────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateStaffAndReturn201() throws Exception {
        when(userUseCase.registerUser(any(RegisterUserCommand.class))).thenReturn(validDoctor());

        mockMvc.perform(post("/api/v1/users/admin/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Dr. Smith",
                                  "email": "smith@example.com",
                                  "password": "temp123",
                                  "phone": "8888888888",
                                  "role": "DOCTOR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("DOCTOR"))
                .andExpect(jsonPath("$.passwordChanged").value(false));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturn403WhenNonAdminCreatesStaff() throws Exception {
        mockMvc.perform(post("/api/v1/users/admin/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Dr. Smith",
                                  "email": "smith@example.com",
                                  "password": "temp123",
                                  "phone": "8888888888",
                                  "role": "DOCTOR"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    // ─── Get by ID (ADMIN only) ───────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByIdAndReturn200() throws Exception {
        when(userUseCase.getUserById(1L)).thenReturn(validPatient());

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.passwordChanged").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        when(userUseCase.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 99"));
    }

    @Test
    void shouldReturn403WhenUnauthenticatedUserAccessesById() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isForbidden());
    }

    // ─── Get by Email (ADMIN only) ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByEmailAndReturn200() throws Exception {
        when(userUseCase.getUserByEmail("john@example.com")).thenReturn(validPatient());

        mockMvc.perform(get("/api/v1/users/by-email")
                        .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    // ─── Update Phone (authenticated) ────────────────────────────────────────

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldUpdatePhoneAndReturn200() throws Exception {
        User updatedUser = new User(1L, "John Doe", "john@example.com", "encodedPassword", "8888888888", Role.PATIENT, true);
        when(userUseCase.updatePhone(any(UpdatePhoneCommand.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/v1/users/1/phone")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "8888888888"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("8888888888"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturn400WhenPhoneIsBlank() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1/phone")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // ─── Update Password (authenticated) ─────────────────────────────────────

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldUpdatePasswordAndReturn204() throws Exception {
        when(userUseCase.updatePassword(any(UpdatePasswordCommand.class))).thenReturn(validPatient());

        mockMvc.perform(patch("/api/v1/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "doctor123",
                                  "newPassword": "newpassword123"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturn400WhenNewPasswordSameAsOld() throws Exception {
        when(userUseCase.updatePassword(any(UpdatePasswordCommand.class)))
                .thenThrow(new IllegalArgumentException("New password cannot be same as old password"));

        mockMvc.perform(patch("/api/v1/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "doctor123",
                                  "newPassword": "doctor123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturn400WhenOldPasswordIsWrong() throws Exception {
        when(userUseCase.updatePassword(any(UpdatePasswordCommand.class)))
                .thenThrow(new IllegalArgumentException("Old password is incorrect"));

        mockMvc.perform(patch("/api/v1/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "wrongpassword",
                                  "newPassword": "newpassword123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
