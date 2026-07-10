package com.healthcare.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to book an appointment slot that
 * is already taken, preventing a double-booking.
 *
 * <p>Mapped to {@code 409 CONFLICT}.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}
