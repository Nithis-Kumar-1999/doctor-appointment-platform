package com.healthcare.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for a Patient's personal profile.
 *
 * <p>Flattens the {@code Patient} + {@code User} entities into a single
 * API response object. Enum fields are returned as both their name
 * (machine-readable) and display name (UI-ready).
 *
 * @param id                 the Patient's database id
 * @param userId             the associated User's database id
 * @param firstName          from User
 * @param lastName           from User
 * @param email              from User
 * @param dateOfBirth        patient's date of birth
 * @param gender             the gender enum name (e.g., {@code "FEMALE"})
 * @param genderDisplayName  the human-readable gender label (e.g., {@code "Female"})
 * @param phone              primary contact number
 * @param address            residential address (null if not set)
 * @param bloodGroup         blood group (null if not set)
 * @param emergencyContact   emergency contact (null if not set)
 * @param active             whether the patient's profile is active
 * @param createdAt          when the profile was first created
 */
public record PatientResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        String gender,
        String genderDisplayName,
        String phone,
        String address,
        String bloodGroup,
        String emergencyContact,
        boolean active,
        LocalDateTime createdAt
) {}
