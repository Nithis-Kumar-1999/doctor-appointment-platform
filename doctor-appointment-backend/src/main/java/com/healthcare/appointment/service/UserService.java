package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.request.RegisterRequest;
import com.healthcare.appointment.entity.User;

public interface UserService {
    
    User registerUser(RegisterRequest request);
    
    User getUserById(Long id);
    
    User getUserByEmail(String email);
}
