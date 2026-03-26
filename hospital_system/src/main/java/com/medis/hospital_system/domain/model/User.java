package com.medis.hospital_system.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private boolean passwordChanged;

    @JsonCreator
    public User(@JsonProperty("id")       Long id,
                @JsonProperty("name")     String name,
                @JsonProperty("email")    String email,
                @JsonProperty("password") String password,
                @JsonProperty("phone")    String phone,
                @JsonProperty("role")     Role role,
                @JsonProperty("passwordChanged")  boolean passwordChanged) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (role == null) {
            throw new IllegalArgumentException("User role is required");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.passwordChanged = passwordChanged;
    }

    public void changePhone(String newPhone) {
        if (newPhone == null || newPhone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        this.phone = newPhone;
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }
        this.password = newPassword;
    }

    public void markPasswordChanged() {
        this.passwordChanged = true;
    }

    public boolean isDoctor()  { return role == Role.DOCTOR; }
    public boolean isPatient() { return role == Role.PATIENT; }
    public boolean isAdmin()   { return role == Role.ADMIN; }
}