package com.medis.hospital_system.application.port.out;

import com.medis.hospital_system.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);
}