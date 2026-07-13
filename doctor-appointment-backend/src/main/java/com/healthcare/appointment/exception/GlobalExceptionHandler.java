package com.healthcare.appointment.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that catches all exceptions thrown by controllers
 * or services and translates them into a standardised {@link ErrorResponse}.
 *
 * <p>This ensures the frontend never receives an HTML error page or an
 * unformatted stack trace, even on unhandled 500 Server Errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 404 Not Found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request, null);
    }

    /**
     * Handles 409 Conflict exceptions (duplicate data).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {
        
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request, null);
    }

    /**
     * Handles 409 Conflict exceptions (appointment double-booking).
     */
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentConflictException(
            AppointmentConflictException ex, HttpServletRequest request) {
        
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request, null);
    }

    /**
     * Handles 400 Bad Request exceptions (business rule violations).
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(
            InvalidOperationException ex, HttpServletRequest request) {
        
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request, null);
    }

    /**
     * Handles 400 Bad Request validation errors from @Valid on request DTOs.
     * Extracts field-level error messages and puts them in the details map.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return buildErrorResponse("Validation failed for the request.", HttpStatus.BAD_REQUEST, request, errors);
    }

    /**
     * Fallback handler for any uncaught RuntimeExceptions.
     * Maps to 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        // Print the actual exception in Render logs
        ex.printStackTrace();

        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                null);
    }

    /**
     * Helper method to construct the consistent ErrorResponse.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message, HttpStatus status, HttpServletRequest request, Map<String, String> details) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
