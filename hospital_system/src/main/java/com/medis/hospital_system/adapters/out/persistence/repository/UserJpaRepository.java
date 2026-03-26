package com.medis.hospital_system.adapters.out.persistence.repository;

import com.medis.hospital_system.adapters.out.persistence.entity.UserEntity;
import com.medis.hospital_system.domain.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone); // was missing — required by domain UserRepository

    boolean existsByEmail(String email);

    List<UserEntity> findByRole(Role role);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<UserEntity> searchUsers(@Param("search") String search, Pageable pageable);
}