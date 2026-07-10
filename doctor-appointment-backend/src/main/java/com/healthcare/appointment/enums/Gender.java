package com.healthcare.appointment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Biological or self-identified gender of a patient.
 *
 * <p>Stored as {@code VARCHAR} via {@code @Enumerated(EnumType.STRING)}
 * on the {@link com.healthcare.appointment.entity.Patient} entity.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other / Prefer not to say");

    /** Human-readable label for UI and reports. */
    private final String displayName;
}
