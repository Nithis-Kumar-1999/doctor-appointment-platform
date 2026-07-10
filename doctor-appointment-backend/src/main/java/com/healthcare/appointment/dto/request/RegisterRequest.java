package com.healthcare.appointment.dto.request;

import com.healthcare.appointment.enums.Role;
import jakarta.validation.constraints.*;

/**
 * Request DTO for new user registration.
 *
 * <p>The {@code role} field accepts only {@code DOCTOR} or {@code PATIENT}.
 * {@code ADMIN} accounts are created internally by an existing ADMIN —
 * this rule is enforced in the service layer, not here, because Jakarta
 * Validation has no built-in constraint for "allowed enum subset."
 *
 * <p>The raw {@code password} is validated here for length and complexity.
 * The {@code AuthService} hashes it with BCrypt before persisting.
 *
 * @param firstName the user's first name
 * @param lastName  the user's last name
 * @param email     the user's email — used as login identifier; must be unique
 * @param password  plain-text password (min 8 chars); hashed before storage
 * @param role      DOCTOR or PATIENT (ADMIN not permitted via self-registration)
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Payload for registering a new user")
public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @NotNull(message = "Role is required")
        Role role

) {}
