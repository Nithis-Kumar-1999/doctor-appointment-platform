package com.healthcare.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource that violates
 * a uniqueness constraint (e.g., registering an existing email,
 * creating a duplicate profile).
 *
 * <p>Mapped to {@code 409 CONFLICT}.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
}
