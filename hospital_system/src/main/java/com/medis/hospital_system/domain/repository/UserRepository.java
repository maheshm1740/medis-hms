package com.medis.hospital_system.domain.repository;

import com.medis.hospital_system.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    Page<User> findAll(int page, int size);

    Page<User> searchUsers(String search, int page, int size);
}
