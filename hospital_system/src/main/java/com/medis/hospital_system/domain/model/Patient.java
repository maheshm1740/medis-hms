package com.medis.hospital_system.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
public class Patient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Set<String> VALID_BLOOD_GROUPS = Set.of(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    );

    private Long id;
    private Long userId;
    private String bloodGroup;
    private String address;
    private String emergencyContact;

    @JsonCreator
    public Patient(@JsonProperty("id")               Long id,
                   @JsonProperty("userId")           Long userId,
                   @JsonProperty("bloodGroup")       String bloodGroup,
                   @JsonProperty("address")          String address,
                   @JsonProperty("emergencyContact") String emergencyContact) {

        if (userId == null) {
            throw new IllegalArgumentException("Patient must be linked to a user");
        }
        if (bloodGroup == null || !VALID_BLOOD_GROUPS.contains(bloodGroup)) {
            throw new IllegalArgumentException("Invalid blood group");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (emergencyContact == null || emergencyContact.isBlank()) {
            throw new IllegalArgumentException("Emergency contact is required");
        }

        this.id = id;
        this.userId = userId;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }

    public void updateAddress(String newAddress) {
        if (newAddress == null || newAddress.isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = newAddress;
    }

    public void updateEmergencyContact(String contact) {
        if (contact == null || contact.isBlank()) {
            throw new IllegalArgumentException("Emergency contact cannot be empty");
        }
        this.emergencyContact = contact;
    }

    public boolean hasEmergencyContact() {
        return emergencyContact != null && !emergencyContact.isBlank();
    }
}