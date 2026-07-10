package com.healthcare.appointment.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardised JSON error response returned by the {@link GlobalExceptionHandler}.
 *
 * <p>Ensures that regardless of what error occurs, the frontend always receives
 * a consistent, predictable structure.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code (e.g., 400, 404, 500)
 * @param error     HTTP status description (e.g., "Bad Request")
 * @param message   human-readable error message safe for UI display
 * @param path      the request URI that triggered the error
 * @param details   optional map for field-level validation errors (null if none)
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {}
