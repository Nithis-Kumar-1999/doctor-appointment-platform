package com.healthcare.appointment.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for booking a new appointment.
 *
 * <p>The authenticated patient's profile is resolved from the JWT token
 * in the service layer — the patientId is NOT provided in the request body.
 * This prevents a patient from booking appointments on behalf of other patients.
 *
 * <p>{@code @Future} on {@code appointmentDate} is placed here on the request DTO
 * (not on the entity) because it should only fire at booking time. This allows
 * the entity to be updated after the appointment date has passed
 * (e.g., status changed to COMPLETED).
 *
 * @param doctorId        the id of the Doctor to book with
 * @param appointmentDate the date of the appointment — must be a future date
 * @param appointmentTime the requested time slot (e.g., 10:30)
 * @param reason          reason for the visit — helps the doctor prepare
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Payload for booking an appointment")
public record AppointmentRequest(

        @NotNull(message = "Doctor ID is required")
        Long doctorId,

        @NotNull(message = "Appointment date is required")
        @Future(message = "Appointment date must be a future date")
        LocalDate appointmentDate,

        @NotNull(message = "Appointment time is required")
        LocalTime appointmentTime,

        @NotBlank(message = "Reason for visit is required")
        @Size(max = 1000, message = "Reason must not exceed 1000 characters")
        String reason

) {}
