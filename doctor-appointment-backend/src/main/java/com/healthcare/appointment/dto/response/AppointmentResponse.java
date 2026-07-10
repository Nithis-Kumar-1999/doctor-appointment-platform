package com.healthcare.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for an Appointment.
 *
 * <p>Flattens the {@code Appointment}, {@code Doctor}, {@code Patient},
 * and their associated {@code User} records into a single self-contained
 * API response. The service layer handles all entity traversal and
 * name concatenation — this record is a pure data carrier.
 *
 * <p>Both doctor and patient are represented by their id and full name
 * (first + last concatenated by the service), avoiding the need for
 * nested response objects that would require additional API calls to resolve.
 *
 * @param id                   the Appointment's database id
 * @param doctorId             the Doctor's database id
 * @param doctorName           the Doctor's full name (firstName + " " + lastName)
 * @param doctorSpecialty      the Doctor's specialty display name
 * @param patientId            the Patient's database id
 * @param patientName          the Patient's full name (firstName + " " + lastName)
 * @param appointmentDate      the scheduled date
 * @param appointmentTime      the scheduled time
 * @param status               the appointment status enum name (e.g., {@code "PENDING"})
 * @param statusDisplayName    the human-readable status label (e.g., {@code "Pending"})
 * @param reason               patient's stated reason for the visit
 * @param notes                doctor's post-appointment notes (null until COMPLETED)
 * @param cancellationReason   reason for cancellation (null unless CANCELLED)
 * @param createdAt            when the appointment was booked
 */
public record AppointmentResponse(
        Long id,
        Long doctorId,
        String doctorName,
        String doctorSpecialty,
        Long patientId,
        String patientName,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String status,
        String statusDisplayName,
        String reason,
        String notes,
        String cancellationReason,
        LocalDateTime createdAt
) {}
