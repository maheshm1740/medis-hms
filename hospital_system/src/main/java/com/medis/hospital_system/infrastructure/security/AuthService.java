package com.medis.hospital_system.infrastructure.security;

import com.medis.hospital_system.adapters.in.web.dto.request.LoginRequest;
import com.medis.hospital_system.adapters.in.web.dto.request.RefreshTokenRequest;
import com.medis.hospital_system.adapters.in.web.dto.response.AuthResponse;
import com.medis.hospital_system.domain.model.User;
import com.medis.hospital_system.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsServiceImpl userDetailsService,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        // fetch full user to get passwordChanged flag
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken  = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("");

        return AuthResponse.of(user.getId(),accessToken, refreshToken, request.email(), role, user.isPasswordChanged());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String email;
        try {
            email = jwtTokenProvider.extractUsername(request.refreshToken());
        } catch (JwtException e) {
            throw new JwtException("Refresh token is invalid or expired");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtTokenProvider.isTokenValid(request.refreshToken(), userDetails)) {
            throw new JwtException("Refresh token is invalid or expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newAccessToken = jwtTokenProvider.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("");

        return AuthResponse.of(user.getId(),newAccessToken, request.refreshToken(), email, role, user.isPasswordChanged());
    }
}