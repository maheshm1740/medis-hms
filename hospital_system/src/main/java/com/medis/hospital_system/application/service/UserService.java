package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.RegisterUserCommand;
import com.medis.hospital_system.application.command.UpdatePasswordCommand;
import com.medis.hospital_system.application.command.UpdatePhoneCommand;
import com.medis.hospital_system.application.port.in.UserUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.UserRepository;
import com.medis.hospital_system.infrastructure.cache.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateResourceException("A user with this email already exists: " + command.email());
        }
        User user = new User(
                null,
                command.name(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.phone(),
                command.role(),
                command.passwordChanged()   // passed in from controller
        );
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = CacheNames.USERS, key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    @Override
    @Cacheable(value = CacheNames.USERS, key = "'email:' + #email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.ofEmail(email));
    }

    @Override
    @Cacheable(value = CacheNames.USERS, key = "'phone:' + #phone")
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phone));
    }

    @Override
    public Page<User> getAllUsers(String search, int page, int size) {

        if (search == null || search.isBlank()) {
            return userRepository.findAll(page, size);
        } else {
            return userRepository.searchUsers(search, page, size);
        }
    }

    @Override
    @Caching(
            put  = { @CachePut(value = CacheNames.USERS, key = "#command.userId()") },
            evict = {
                    @CacheEvict(value = CacheNames.USERS, key = "'email:' + #result.email"),
                    @CacheEvict(value = CacheNames.USERS, key = "'phone:' + #result.phone")
            }
    )
    public User updatePhone(UpdatePhoneCommand command) {
        User user = getUserById(command.userId());
        user.changePhone(command.newPhone());
        return userRepository.save(user);
    }

    @Override
    @Caching(
            put   = { @CachePut(value = CacheNames.USERS, key = "#command.userId()") },
            evict = {
                    @CacheEvict(value = CacheNames.USERS, key = "'email:' + #result.email"),
                    @CacheEvict(value = CacheNames.USERS, key = "'phone:' + #result.phone")
            }
    )
    public User updatePassword(UpdatePasswordCommand command) {
        User user = getUserById(command.userId());

        if (!passwordEncoder.matches(command.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        if (passwordEncoder.matches(command.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be same as old password");
        }

        user.changePassword(passwordEncoder.encode(command.newPassword()));
        user.markPasswordChanged();
        return userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}