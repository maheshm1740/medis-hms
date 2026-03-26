package com.medis.hospital_system.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "patients",
        indexes = {
                @Index(name = "idx_patient_user", columnList = "user_id")
        }
)
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(nullable = false)
    private String bloodGroup;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String emergencyContact;

    public PatientEntity() {}

    public PatientEntity(Long id, UserEntity user, String bloodGroup,
                         String address, String emergencyContact) {
        this.id = id;
        this.user = user;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }
}