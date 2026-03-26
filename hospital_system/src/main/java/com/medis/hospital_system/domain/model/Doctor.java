package com.medis.hospital_system.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
public class Doctor implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private List<String> specialization;
    private String department;
    private String licenseNumber;
    private int experienceYears;

    @JsonCreator
    public Doctor(@JsonProperty("id")              Long id,
                  @JsonProperty("userId")          Long userId,
                  @JsonProperty("specialization")  List<String> specialization,
                  @JsonProperty("department")      String department,
                  @JsonProperty("licenseNumber")   String licenseNumber,
                  @JsonProperty("experienceYears") int experienceYears) {

        if (userId == null) {
            throw new IllegalArgumentException("Doctor must be linked to a user");
        }
        if (specialization == null || specialization.isEmpty()) {
            throw new IllegalArgumentException("Specialization is required");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("License number is required");
        }
        if (experienceYears < 0) {
            throw new IllegalArgumentException("Experience years cannot be negative");
        }

        this.id = id;
        this.userId = userId;
        this.specialization = List.copyOf(specialization);
        this.department = department;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
    }

    public void updateDepartment(String newDepartment) {
        if (newDepartment == null || newDepartment.isBlank()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        this.department = newDepartment;
    }

    public void updateSpecialization(List<String> newSpecialization) {
        if (newSpecialization == null || newSpecialization.isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be empty");
        }
        this.specialization = List.copyOf(newSpecialization);
    }

    public boolean isExperiencedDoctor() {
        return experienceYears >= 10;
    }
}