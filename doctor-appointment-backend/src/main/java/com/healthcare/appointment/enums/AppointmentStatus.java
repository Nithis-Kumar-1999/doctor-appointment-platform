package com.healthcare.appointment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Lifecycle states of an {@link com.healthcare.appointment.entity.Appointment}.
 *
 * <p>State transitions:
 * <pre>
 *   PENDING → CONFIRMED → COMPLETED
 *      │           │
 *      └───────────┴──→ CANCELLED
 * </pre>
 *
 * <ul>
 *   <li>{@code PENDING} — Initial state on booking. Awaiting confirmation.</li>
 *   <li>{@code CONFIRMED} — Doctor or Admin has confirmed the appointment.</li>
 *   <li>{@code COMPLETED} — Appointment took place successfully.</li>
 *   <li>{@code CANCELLED} — Cancelled by Patient, Doctor, or Admin.</li>
 * </ul>
 *
 * <p>Transition rules are enforced in the service layer, not here.
 * The enum only defines the valid states.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AppointmentStatus {

    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    /** Human-readable label for UI and reports. */
    private final String displayName;
}
