package com.healthcare.appointment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * JPA Entity representing a Doctor's weekly availability schedule.
 *
 * <p>Each row defines the hours a Doctor is available on a specific day of the week.
 * The appointment booking flow uses this to derive bookable time slots
 * (e.g., 09:00–09:30, 09:30–10:00...) by dividing the window
 * [{@code startTime}, {@code endTime}] into {@code slotDurationMinutes} intervals.
 *
 * <h2>Why {@code java.time.DayOfWeek} and not a custom enum?</h2>
 * <p>Java's built-in {@code java.time.DayOfWeek} is complete:
 * <ul>
 *   <li>{@code .name()} → {@code "MONDAY"} — stored as VARCHAR via {@code EnumType.STRING}</li>
 *   <li>{@code .getDisplayName(TextStyle.FULL, Locale.ENGLISH)} → {@code "Monday"} — for UI</li>
 *   <li>{@code .getValue()} → {@code 1} — for calendar arithmetic</li>
 * </ul>
 * A custom DayOfWeek enum would duplicate this, conflict with the JDK type in imports,
 * and provide no additional value. Using the JDK type directly is the simpler, correct choice.
 *
 * <h2>Unique Constraint: {@code (doctor_id, day_of_week)}</h2>
 * <p>A doctor can have at most one availability window per day. Multiple entries
 * for the same day would create ambiguity in slot calculation. The unique constraint
 * enforces this at the database level. The composite index on {@code (doctor_id, day_of_week)}
 * created by this constraint also serves the most common lookup query:
 * "fetch all availability entries for a given doctor."
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "doctor")
@Entity
@Table(
        name = "doctor_availabilities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_availability_doctor_day",
                        columnNames = {"doctor_id", "day_of_week"}
                )
        }
        /*
         * No separate @Index declarations needed.
         * MySQL automatically creates an index on (doctor_id, day_of_week)
         * to enforce the unique constraint above. By the MySQL prefix rule,
         * this composite index also covers doctor_id-only queries
         * (e.g., "find all slots for doctor X"), making an extra
         * single-column index on doctor_id redundant.
         */
)
public class DoctorAvailability extends BaseEntity {

    // =========================================================================
    // RELATIONSHIPS
    // =========================================================================

    /**
     * The {@link Doctor} this availability entry belongs to.
     *
     * <p>{@code @ManyToOne}: Many availability entries can belong to one Doctor
     * (one entry per day, up to 7 per doctor).
     *
     * <p>{@code FetchType.LAZY}: When loading an availability entry,
     * the Doctor is not needed unless explicitly accessed. Without LAZY,
     * every availability query would also load the Doctor and potentially
     * trigger a further load of the Doctor's associated User.
     *
     * <p>{@code optional = false}: An availability entry without a Doctor
     * is meaningless. This also allows Hibernate to use a proxy,
     * making LAZY effective on this owning side.
     */
    @NotNull(message = "Doctor is required for availability entry")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_availability_doctor_id")
    )
    private Doctor doctor;

    // =========================================================================
    // SCHEDULE FIELDS
    // =========================================================================

    /**
     * Day of the week this availability applies to.
     *
     * <p>Uses {@code java.time.DayOfWeek} stored as {@code EnumType.STRING}.
     * The stored value is the enum constant name: {@code "MONDAY"}, {@code "TUESDAY"}, etc.
     *
     * <p>{@code length = 9}: Long enough for {@code "WEDNESDAY"} (9 chars), the longest value.
     */
    @NotNull(message = "Day of week is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 9)
    private DayOfWeek dayOfWeek;

    /**
     * Time when the Doctor's availability window begins (e.g., 09:00).
     *
     * <p>Uses {@code LocalTime} — there is no date or timezone component
     * for a recurring weekly schedule. The booking service combines this
     * with the appointment's {@code LocalDate} to form a full {@code LocalDateTime}.
     *
     * <p>Cross-field validation ({@code startTime < endTime}) is enforced
     * in the service layer, not here. Entities validate individual field
     * constraints only; inter-field rules belong in business logic.
     */
    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * Time when the Doctor's availability window ends (e.g., 17:00).
     *
     * <p>Must be after {@code startTime} — validated in the service layer.
     */
    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Duration of each appointment slot in minutes (e.g., 15, 30, 60).
     *
     * <p>Uses {@code Integer} (wrapper) so {@code @NotNull} is meaningful.
     * A primitive {@code int} defaults to {@code 0} and cannot be null,
     * making {@code @NotNull} a silent no-op on primitive types.
     *
     * <p>{@code @Min(10)}: A 10-minute slot is the practical minimum for
     * a medical consultation. Shorter slots cannot accommodate a real appointment.
     *
     * <p>{@code @Max(480)}: 8 hours is the practical maximum — prevents
     * data entry errors while covering edge cases like full-day procedures.
     */
    @NotNull(message = "Slot duration is required")
    @Min(value = 10, message = "Slot duration must be at least 10 minutes")
    @Max(value = 480, message = "Slot duration must not exceed 480 minutes")
    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes;

    // =========================================================================
    // STATUS
    // =========================================================================

    /**
     * Whether this availability entry is currently active.
     *
     * <p>An inactive entry means the doctor is not available on this day
     * (e.g., temporary day-off or schedule change) without deleting the record.
     * {@code @Builder.Default} ensures the builder initialises this to {@code true}.
     */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // =========================================================================
    // equals() and hashCode()
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorAvailability other)) return false;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
