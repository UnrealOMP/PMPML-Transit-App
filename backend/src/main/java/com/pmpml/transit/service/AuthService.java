package com.pmpml.transit.service;

import com.pmpml.transit.dto.request.LoginRequest;
import com.pmpml.transit.dto.request.RefreshTokenRequest;
import com.pmpml.transit.dto.request.RegisterRequest;
import com.pmpml.transit.dto.response.AuthResponse;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.enums.UserRole;
import com.pmpml.transit.exception.ConflictException;
import com.pmpml.transit.exception.UnauthorizedException;
import com.pmpml.transit.repository.UserRepository;
import com.pmpml.transit.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        var user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(UserRole.PASSENGER))
                .build();
        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());
        return buildAuthResponse(user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        return buildAuthResponse(request.getEmail());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String email = jwtService.extractUsername(request.getRefreshToken());
        if (email == null) throw new UnauthorizedException("Invalid refresh token");
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        return buildAuthResponse(email);
    }

    private AuthResponse buildAuthResponse(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        var user = userRepository.findByEmail(email).orElseThrow();
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs())
                .userId(user.getId().toString())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}
