package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * JPA Entity representing a booked appointment between a {@link Patient} and a {@link Doctor}.
 *
 * <p>An Appointment records:
 * <ul>
 *   <li>Who booked it ({@link Patient}) and with whom ({@link Doctor})</li>
 *   <li>When it is scheduled ({@code appointmentDate} + {@code appointmentTime})</li>
 *   <li>Why the patient is visiting ({@code reason})</li>
 *   <li>Its current lifecycle state ({@code status})</li>
 *   <li>Post-appointment notes ({@code notes}) and cancellation reason if applicable</li>
 * </ul>
 *
 * <h2>Unique Constraint: {@code (doctor_id, appointment_date, appointment_time)}</h2>
 * <p>A doctor can have at most one appointment at any given date-time slot.
 * This prevents double-booking at the database level. The composite index
 * created by this constraint also covers all doctor-specific appointment
 * queries ({@code WHERE doctor_id = ?}).
 *
 * <h2>Why no {@code @FutureOrPresent} on {@code appointmentDate}?</h2>
 * <p>Jakarta's {@code @FutureOrPresent} fires on every call to {@code save()},
 * including status updates (CONFIRMED, COMPLETED) that happen after the appointment
 * date has passed. Placing it on the entity would make it impossible to mark a
 * past appointment as COMPLETED. Date-in-future validation belongs in the
 * service layer at booking time only.
 *
 * <h2>Separation of Date and Time</h2>
 * <p>{@code appointmentDate} ({@code LocalDate}) and {@code appointmentTime}
 * ({@code LocalTime}) are stored in separate columns rather than a single
 * {@code LocalDateTime}. This mirrors the {@code DoctorAvailability} model
 * and makes date-range queries (e.g., "appointments for this week")
 * simpler with standard SQL {@code DATE} comparisons.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctor", "patient"})
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = {
                /*
                 * A doctor cannot have two appointments at the same date and time.
                 * Prevents double-booking at the DB level even under concurrent requests.
                 * The composite index from this constraint also covers
                 * doctor_id-only lookups via the MySQL leftmost prefix rule.
                 */
                @UniqueConstraint(
                        name = "uk_appointments_doctor_date_time",
                        columnNames = {"doctor_id", "appointment_date", "appointment_time"}
                )
        },
        indexes = {
                /*
                 * idx_appointments_patient_id: Required for the primary patient query:
                 * "Get all appointments for this patient."
                 * Without this, every call to /api/v1/appointments/my performs
                 * a full table scan on the appointments table.
                 */
                @Index(name = "idx_appointments_patient_id", columnList = "patient_id")
        }
)
public class Appointment extends BaseEntity {

    // =========================================================================
    // RELATIONSHIPS
    // =========================================================================

    /**
     * The Doctor with whom the appointment is booked.
     *
     * <p>{@code @ManyToOne(LAZY)}: Many appointments can belong to one Doctor.
     * Lazy loading prevents loading the full Doctor (and transitively the User)
     * on every appointment query.
     */
    @NotNull(message = "Doctor is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointments_doctor_id")
    )
    private Doctor doctor;

    /**
     * The Patient who booked the appointment.
     *
     * <p>{@code @ManyToOne(LAZY)}: Many appointments can belong to one Patient.
     */
    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointments_patient_id")
    )
    private Patient patient;

    // =========================================================================
    // SCHEDULE
    // =========================================================================

    /**
     * The calendar date of the appointment.
     *
     * <p>Uses {@code LocalDate} — stored as SQL {@code DATE}.
     * Future-date validation (must book in the future) is enforced
     * in the service layer at booking time, not here.
     */
    @NotNull(message = "Appointment date is required")
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    /**
     * The clock time of the appointment slot (e.g., 10:30).
     *
     * <p>Uses {@code LocalTime} — stored as SQL {@code TIME}.
     * Stored separately from {@code appointmentDate} to align with
     * the {@code DoctorAvailability} model and simplify date-range queries.
     */
    @NotNull(message = "Appointment time is required")
    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    // =========================================================================
    // STATUS
    // =========================================================================

    /**
     * Current lifecycle state of the appointment.
     *
     * <p>Defaults to {@code PENDING} on creation.
     * {@code @Builder.Default} is required — without it, the builder
     * would leave this field {@code null} (an object field default), causing
     * a {@code NOT NULL} constraint violation on the first save.
     *
     * <p>State transitions (PENDING → CONFIRMED → COMPLETED, or either → CANCELLED)
     * are validated in the service layer.
     */
    @Builder.Default
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    // =========================================================================
    // CONTENT FIELDS
    // =========================================================================

    /**
     * Reason for the visit, provided by the patient at booking time.
     *
     * <p>Required — helps the doctor prepare for the appointment.
     * Stored as {@code TEXT} to accommodate detailed descriptions.
     */
    @NotBlank(message = "Reason for visit is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    /**
     * Clinical notes added by the Doctor after the appointment. Optional — nullable.
     *
     * <p>Populated when status transitions to {@code COMPLETED}.
     */
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Reason provided when the appointment is cancelled. Optional — nullable.
     *
     * <p>Populated when status transitions to {@code CANCELLED}.
     */
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // =========================================================================
    // equals() and hashCode()
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment other)) return false;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
