package com.healthcare.appointment.service;

import com.healthcare.appointment.entity.RefreshToken;
import com.healthcare.appointment.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUserId(Long userId);
}
