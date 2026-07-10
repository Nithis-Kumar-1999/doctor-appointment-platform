package com.healthcare.appointment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Medical specialties supported by the Doctor Appointment system.
 *
 * <h2>Design Decisions</h2>
 *
 * <h3>1. Enum vs Database Lookup Table</h3>
 * <p>Specialties are defined as a Java enum rather than a separate {@code specialties}
 * database table. This is intentional:
 * <ul>
 *   <li>Specialties are a <b>closed, stable domain</b> — new medical specialties
 *       do not appear frequently and always require application-level changes
 *       (search filters, UI dropdowns, business rules) anyway.</li>
 *   <li>An enum eliminates an extra JOIN on every doctor query.</li>
 *   <li>Enum values are validated at compile time — an invalid specialty cannot
 *       exist in the system. A DB lookup table requires runtime validation.</li>
 * </ul>
 *
 * <h3>2. {@code EnumType.STRING} on the owning entity</h3>
 * <p>The Doctor entity uses {@code @Enumerated(EnumType.STRING)} to store
 * {@code "CARDIOLOGY"} instead of the ordinal {@code 1}. This is mandatory —
 * see {@link Role} Javadoc for the full explanation.
 *
 * <h3>3. {@code displayName} for UI</h3>
 * <p>The DB stores {@code "ENT"} but the frontend and email templates need
 * {@code "Ear, Nose & Throat"}. Having it on the enum is the single source
 * of truth — no switch-case mapping anywhere else in the codebase.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum Specialty {

    GENERAL_MEDICINE("General Medicine"),
    CARDIOLOGY("Cardiology"),
    DERMATOLOGY("Dermatology"),
    NEUROLOGY("Neurology"),
    ORTHOPEDICS("Orthopedics"),
    PEDIATRICS("Pediatrics"),
    GYNECOLOGY("Gynecology & Obstetrics"),
    OPHTHALMOLOGY("Ophthalmology"),
    DENTISTRY("Dentistry"),
    PSYCHIATRY("Psychiatry"),
    RADIOLOGY("Radiology"),
    GENERAL_SURGERY("General Surgery"),
    ENT("Ear, Nose & Throat"),
    UROLOGY("Urology"),
    NEPHROLOGY("Nephrology"),
    ENDOCRINOLOGY("Endocrinology"),
    ONCOLOGY("Oncology"),
    GASTROENTEROLOGY("Gastroenterology"),
    RHEUMATOLOGY("Rheumatology"),
    PULMONOLOGY("Pulmonology");

    /**
     * Human-readable label for UI rendering, email templates, and API responses.
     */
    private final String displayName;
}
