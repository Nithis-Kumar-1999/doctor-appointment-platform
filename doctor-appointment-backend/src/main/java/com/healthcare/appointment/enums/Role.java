package com.healthcare.appointment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the three roles supported by the application.
 *
 * <h2>Design Decisions</h2>
 *
 * <h3>1. Why {@code EnumType.STRING} (not {@code EnumType.ORDINAL})?</h3>
 * <p>When JPA persists an enum using {@code @Enumerated(EnumType.ORDINAL)},
 * it stores the integer position (0, 1, 2...). This is catastrophically fragile:
 * adding a new enum constant in the middle of the declaration silently shifts
 * all ordinal values, corrupting existing data with no error.
 * {@code EnumType.STRING} stores {@code "ADMIN"}, {@code "DOCTOR"}, {@code "PATIENT"}
 * as plain text — readable, stable, and refactoring-safe.
 * The column is mapped with {@code @Enumerated(EnumType.STRING)} on the owning entity.
 *
 * <h3>2. Why store a {@code displayName}?</h3>
 * <p>The JPA column stores {@code "ADMIN"} (the enum name). However, the UI and
 * email templates require a human-readable label like {@code "Administrator"}.
 * Having {@code displayName} on the enum eliminates the need for a switch-case
 * or if-else mapping anywhere else in the codebase — single source of truth.
 *
 * <h3>3. Why {@code @Getter} and {@code @RequiredArgsConstructor} from Lombok?</h3>
 * <p>{@code @Getter} generates the {@code getDisplayName()} accessor.
 * {@code @RequiredArgsConstructor} generates the constructor that accepts
 * the {@code final String displayName} field, which is required by enum syntax.
 * This removes boilerplate while keeping the enum immutable (final field).
 *
 * <h3>4. Future: Spring Security Integration</h3>
 * <p>In Commit 5 (Spring Security setup), this enum will implement
 * {@code org.springframework.security.core.GrantedAuthority} and override
 * {@code getAuthority()} to return {@code "ROLE_ADMIN"}, {@code "ROLE_DOCTOR"},
 * {@code "ROLE_PATIENT"} — the format Spring Security expects for
 * {@code @PreAuthorize("hasRole('ADMIN')")} checks.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    /**
     * System administrator.
     * Full access to all resources: manage doctors, patients, view reports.
     */
    ADMIN("Administrator"),

    /**
     * Medical doctor registered in the system.
     * Can view own schedule, update appointment status, manage availability.
     */
    DOCTOR("Doctor"),

    /**
     * Patient registered in the system.
     * Can search doctors, book appointments, cancel, and view own history.
     */
    PATIENT("Patient");

    // =========================================================================
    // FIELDS
    // =========================================================================

    /**
     * Human-readable label for UI rendering and email templates.
     * {@code final} ensures immutability — enum constants are value types.
     */
    private final String displayName;
}
