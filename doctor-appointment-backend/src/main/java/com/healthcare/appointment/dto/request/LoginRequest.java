package com.healthcare.appointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user login.
 *
 * <p>Validated by {@code @Valid} in the controller. Spring Security's
 * authentication manager receives the email and raw password, verifies
 * the BCrypt hash against the stored hash, and issues a JWT on success.
 *
 * @param email    the user's registered email address
 * @param password the user's plain-text password (never stored; compared against BCrypt hash)
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Payload for user login credentials")
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        String password

) {}
