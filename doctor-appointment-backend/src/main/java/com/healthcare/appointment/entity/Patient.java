package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA Entity representing a Patient's personal profile.
 *
 * <p>A {@code Patient} is a profile extension of {@link User}, following the same
 * extension-table pattern used by {@link Doctor}. The {@code users} table holds
 * authentication data; the {@code patients} table holds personal/medical data.
 *
 * <p>Optional fields ({@code address}, {@code bloodGroup}, {@code emergencyContact})
 * are nullable — a patient can register with minimal information and complete
 * their profile later.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Entity
@Table(
        name = "patients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_patients_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {
                // Required for the OneToOne FK lookup performed by Hibernate
                @Index(name = "idx_patients_user_id", columnList = "user_id")
        }
)
public class Patient extends BaseEntity {

    // =========================================================================
    // RELATIONSHIPS
    // =========================================================================

    /**
     * The {@link User} account that owns this Patient profile.
     *
     * <p>{@code optional = false} combined with {@code FetchType.LAZY} enables
     * true proxy-based lazy loading on the owning side of a {@code @OneToOne}.
     * Without {@code optional = false}, Hibernate issues an extra SELECT to
     * check for null even when lazy loading is declared.
     */
    @NotNull(message = "Patient must be associated with a user account")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_patients_user_id")
    )
    private User user;

    // =========================================================================
    // PERSONAL INFORMATION
    // =========================================================================

    /**
     * Patient's date of birth.
     *
     * <p>Uses {@code LocalDate} — a birthday has no time component.
     * {@code @Past} ensures the date is strictly before today,
     * preventing invalid entries like a future birth date.
     */
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be a past date")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Patient's gender, stored as a {@code VARCHAR} string.
     */
    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    /**
     * Patient's contact phone number.
     *
     * <p>Validated for common international and domestic formats.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{8,14}$",
            message = "Phone number must be a valid format"
    )
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // =========================================================================
    // OPTIONAL FIELDS
    // =========================================================================

    /**
     * Patient's residential address. Optional — nullable.
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    /**
     * Patient's blood group (e.g., "A+", "O-", "AB+"). Optional — nullable.
     *
     * <p>Stored as a plain String. Common values are short (max 3 characters
     * for standard ABO+Rh notation), but {@code length = 5} provides headroom
     * for extended notations.
     */
    @Size(max = 5, message = "Blood group must not exceed 5 characters")
    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    /**
     * Emergency contact phone number. Optional — nullable.
     *
     * <p>Validated to the same phone format as the primary phone field
     * when a value is provided. Jakarta's {@code @Pattern} only fires
     * when the value is non-null, so null is accepted for this optional field.
     */
    @Pattern(
            regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{8,14}$",
            message = "Emergency contact must be a valid phone number format"
    )
    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    // =========================================================================
    // STATUS
    // =========================================================================

    /**
     * Soft-delete flag. {@code true} = account is active.
     *
     * <p>{@code @Builder.Default} is required so the builder initialises
     * this field to {@code true} instead of the primitive default {@code false}.
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
        if (!(o instanceof Patient other)) return false;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
