package com.medis.hospital_system.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "doctors",
        indexes = {
                @Index(name = "idx_doctor_user", columnList = "user_id"),
                @Index(name = "idx_doctor_license", columnList = "licenseNumber")
        }
)
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @ElementCollection
    @CollectionTable(
            name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id"),
            indexes = {
                    @Index(name = "idx_doctor_specialization", columnList = "specialization")
            }
    )
    @Column(name = "specialization", nullable = false)
    private List<String> specialization;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private int experienceYears;

    public DoctorEntity() {}

    public DoctorEntity(Long id, UserEntity user, List<String> specialization,
                        String department, String licenseNumber, int experienceYears) {
        this.id = id;
        this.user = user;
        this.specialization = specialization;
        this.department = department;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
    }
}