package com.healthcare.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested business operation violates the domain rules
 * (e.g., wrong user role, illegal status transition).
 *
 * <p>Mapped to {@code 400 BAD REQUEST}.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
