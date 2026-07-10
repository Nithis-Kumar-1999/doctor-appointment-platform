package com.healthcare.appointment.service.impl;

import com.healthcare.appointment.entity.RefreshToken;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.repository.RefreshTokenRepository;
import com.healthcare.appointment.security.jwt.JwtProperties;
import com.healthcare.appointment.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Find existing token or create a new one (One-to-One mapping rule)
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(RefreshToken.builder().user(user).build());

        // Generate an opaque random string
        refreshToken.setToken(UUID.randomUUID().toString());
        // Set expiry date using the properties configuration
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            // Token is expired. Delete it from DB and throw exception.
            refreshTokenRepository.delete(token);
            throw new InvalidOperationException("Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
