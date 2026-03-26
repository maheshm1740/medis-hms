package com.medis.hospital_system.adapters.out.persistence.adapter;

import com.medis.hospital_system.adapters.out.persistence.entity.UserEntity;
import com.medis.hospital_system.adapters.out.persistence.mapper.UserMapper;
import com.medis.hospital_system.adapters.out.persistence.repository.UserJpaRepository;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(
                userJpaRepository.save(UserMapper.toEntity(user))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByPhone(String phone) {
        return userJpaRepository.findByPhone(phone)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    public Page<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userJpaRepository.findAll(pageable)
                .map(UserMapper::toDomain);
    }

    @Override
    public Page<User> searchUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userJpaRepository.searchUsers(search, pageable)
                .map(UserMapper::toDomain);
    }
}