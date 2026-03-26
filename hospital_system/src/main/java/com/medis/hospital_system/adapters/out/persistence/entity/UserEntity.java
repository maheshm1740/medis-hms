package com.medis.hospital_system.adapters.out.persistence.entity;

import com.medis.hospital_system.domain.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_phone", columnList = "phone"),
                @Index(name = "idx_user_role", columnList = "role")
        }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean passwordChanged;

    public UserEntity() {}

    public UserEntity(Long id,
                      String name,
                      String email,
                      String password,
                      String phone,
                      Role role,
                      boolean passwordChanged) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.passwordChanged = passwordChanged;
    }
}