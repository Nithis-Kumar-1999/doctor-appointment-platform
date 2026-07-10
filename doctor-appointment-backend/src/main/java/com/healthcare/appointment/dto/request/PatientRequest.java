package com.healthcare.appointment.dto.request;

import com.healthcare.appointment.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating a Patient's personal profile.
 *
 * <p>The associated User account is resolved from the JWT token in the
 * service layer — the patient does not provide their own userId in the body.
 *
 * @param dateOfBirth      patient's date of birth — must be a past date
 * @param gender           patient's gender
 * @param phone            primary contact number
 * @param address          residential address (optional)
 * @param bloodGroup       blood group e.g., "A+", "O-" (optional)
 * @param emergencyContact emergency contact phone number (optional)
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Payload for creating or updating a patient profile")
public record PatientRequest(

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be a past date")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{8,14}$",
                message = "Phone number must be a valid format"
        )
        String phone,

        @Size(max = 500, message = "Address must not exceed 500 characters")
        String address,

        @Size(max = 5, message = "Blood group must not exceed 5 characters")
        String bloodGroup,

        @Pattern(
                regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{8,14}$",
                message = "Emergency contact must be a valid phone number format"
        )
        String emergencyContact

) {}
