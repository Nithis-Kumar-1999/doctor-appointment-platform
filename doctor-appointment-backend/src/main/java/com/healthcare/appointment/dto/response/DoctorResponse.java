package com.healthcare.appointment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for a Doctor's professional profile.
 *
 * <p>Flattens the {@code Doctor} + {@code User} entities into a single
 * API response object. The service layer is responsible for mapping
 * entity fields to this record — no mapping logic lives here.
 *
 * <p>Enum fields ({@code specialty}) are returned as their name string
 * (e.g., {@code "CARDIOLOGY"}) for machine-readable API consumption.
 * The {@code specialtyDisplayName} field provides the human-readable
 * label (e.g., {@code "Cardiology"}) for direct UI rendering without
 * requiring client-side enum mapping.
 *
 * @param id                   the Doctor's database id
 * @param userId               the associated User's database id
 * @param firstName            the doctor's first name (from User)
 * @param lastName             the doctor's last name (from User)
 * @param email                the doctor's email (from User)
 * @param specialty            the specialty enum name (e.g., {@code "CARDIOLOGY"})
 * @param specialtyDisplayName the human-readable specialty label (e.g., {@code "Cardiology"})
 * @param qualification        academic qualifications string
 * @param experienceYears      years of experience
 * @param consultationFee      per-appointment fee in INR
 * @param phone                contact phone number
 * @param city                 city of practice
 * @param bio                  optional biography (null if not set)
 * @param profileImageUrl      optional profile photo URL (null if not set)
 * @param active               whether this doctor's profile is active
 * @param createdAt            when this profile was first created
 */
public record DoctorResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String email,
        String specialty,
        String specialtyDisplayName,
        String qualification,
        Integer experienceYears,
        BigDecimal consultationFee,
        String phone,
        String city,
        String bio,
        String profileImageUrl,
        boolean active,
        LocalDateTime createdAt
) {}
