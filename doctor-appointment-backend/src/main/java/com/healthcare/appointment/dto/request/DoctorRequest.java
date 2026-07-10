package com.healthcare.appointment.dto.request;

import com.healthcare.appointment.enums.Specialty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for creating or updating a Doctor's professional profile.
 *
 * <p>Used by both the create ({@code POST /api/v1/doctors/profile}) and
 * update ({@code PUT /api/v1/doctors/profile}) endpoints.
 * The associated User account is resolved from the JWT token in the service layer,
 * not provided in this request body.
 *
 * @param specialty            the doctor's medical specialty
 * @param qualification        academic and professional qualifications (e.g., "MBBS, MD")
 * @param experienceYears      total years of medical practice
 * @param consultationFee      per-appointment fee in INR (must be &gt; 0)
 * @param phone                contact phone number
 * @param city                 city where the doctor practices
 * @param bio                  optional professional biography
 * @param profileImageUrl      optional URL to the doctor's profile photo
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Payload for creating or updating a doctor profile")
public record DoctorRequest(

        @NotNull(message = "Specialty is required")
        Specialty specialty,

        @NotBlank(message = "Qualification is required")
        @Size(max = 200, message = "Qualification must not exceed 200 characters")
        String qualification,

        @NotNull(message = "Experience years is required")
        @Min(value = 0, message = "Experience years cannot be negative")
        @Max(value = 60, message = "Experience years cannot exceed 60")
        Integer experienceYears,

        @NotNull(message = "Consultation fee is required")
        @DecimalMin(value = "0.01", message = "Consultation fee must be greater than 0")
        @Digits(integer = 8, fraction = 2,
                message = "Consultation fee must have at most 8 integer digits and 2 decimal places")
        BigDecimal consultationFee,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{8,14}$",
                message = "Phone number must be a valid format"
        )
        String phone,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 2000, message = "Bio must not exceed 2000 characters")
        String bio,

        @Size(max = 512, message = "Profile image URL must not exceed 512 characters")
        String profileImageUrl

) {}
