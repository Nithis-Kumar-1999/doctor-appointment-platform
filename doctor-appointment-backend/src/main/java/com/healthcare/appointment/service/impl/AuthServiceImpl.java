package com.healthcare.appointment.service.impl;

import com.healthcare.appointment.dto.request.LoginRequest;
import com.healthcare.appointment.dto.request.RegisterRequest;
import com.healthcare.appointment.dto.response.LoginResponse;
import com.healthcare.appointment.entity.RefreshToken;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.repository.RefreshTokenRepository;
import com.healthcare.appointment.repository.UserRepository;
import com.healthcare.appointment.security.CustomUserDetailsService;
import com.healthcare.appointment.security.jwt.JwtService;
import com.healthcare.appointment.service.AuthService;
import com.healthcare.appointment.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        // 1. Create and save the new user with BCrypt hashed password
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        User savedUser = userRepository.save(user);

        // 2. Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        // 3. Return response
        return new LoginResponse(
                jwtToken,
                refreshToken.getToken(),
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getRole().name()
        );
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate credentials via Spring Security (throws BadCredentialsException if invalid)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. Fetch user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // 4. Return response
        return new LoginResponse(
                jwtToken,
                refreshToken.getToken(),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getRole().name()
        );
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshTokenStr) {
        // 1. Find token in DB -> 2. Verify expiration -> 3. Get associated User -> 4. Generate new tokens
        return refreshTokenRepository.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    String accessToken = jwtService.generateToken(userDetails);
                    // Rotate refresh token for security (optional but recommended)
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    
                    return new LoginResponse(
                            accessToken,
                            newRefreshToken.getToken(),
                            "Bearer",
                            user.getId(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getRole().name()
                    );
                })
                .orElseThrow(() -> new InvalidOperationException("Refresh token is missing or invalid"));
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        // Invalidates the current session by deleting the refresh token from the DB
        refreshTokenService.deleteByUserId(userId);
    }
}
