package com.medis.hospital_system.infrastructure.cache;

import com.medis.hospital_system.application.command.UpdatePasswordCommand;
import com.medis.hospital_system.application.command.UpdatePhoneCommand;
import com.medis.hospital_system.application.service.UserService;
import com.medis.hospital_system.domain.model.Role;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.UserRepository;
import com.medis.hospital_system.infrastructure.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
class UserServiceCacheTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private User sampleUser() {
        return new User(1L, "John Doe", "john@example.com", "encodedPassword", "9999999999", Role.PATIENT, true);
    }

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    @Test
    void getUserById_shouldCacheOnFirstCall() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));

        userService.getUserById(1L);
        userService.getUserById(1L);
        userService.getUserById(1L);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByEmail_shouldCacheOnFirstCall() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser()));

        userService.getUserByEmail("john@example.com");
        userService.getUserByEmail("john@example.com");

        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void getUserByPhone_shouldCacheOnFirstCall() {
        when(userRepository.findByPhone("9999999999")).thenReturn(Optional.of(sampleUser()));

        userService.getUserByPhone("9999999999");
        userService.getUserByPhone("9999999999");

        verify(userRepository, times(1)).findByPhone("9999999999");
    }

    @Test
    void updatePhone_shouldEvictAndRepopulateCache() {
        User user = sampleUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.getUserById(1L);
        userService.updatePhone(new UpdatePhoneCommand(1L, "8888888888"));

        // CachePut repopulates — next call should NOT hit DB again
        userService.getUserById(1L);

        // 1 for prime + 1 inside updatePhone (self-call bypasses proxy)
        verify(userRepository, times(2)).findById(1L);
    }

    @Test
    void updatePassword_shouldEvictAndRepopulateCache() {
        User user = sampleUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpass123", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newpass123", "encodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newpass123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.getUserById(1L);
        userService.updatePassword(new UpdatePasswordCommand(1L, "oldpass123", "newpass123"));
        userService.getUserById(1L);

        verify(userRepository, times(2)).findById(1L);
    }

    @Test
    void getUserById_cacheShouldContainEntryAfterFirstCall() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));

        userService.getUserById(1L);

        var cache = cacheManager.getCache(CacheNames.USERS);
        assertThat(cache).isNotNull();
        assertThat(cache.get(1L)).isNotNull();
    }
}
