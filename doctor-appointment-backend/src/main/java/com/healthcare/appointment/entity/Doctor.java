package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Specialty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * JPA Entity representing a Doctor's professional profile.
 *
 * <p>A {@code Doctor} is a <b>profile extension</b> of a {@link User}.
 * Every doctor has exactly one user account (for authentication), and every
 * user with {@code Role.DOCTOR} has exactly one Doctor profile (for professional data).
 * This is a classic <b>1-to-1 extension table pattern</b>:
 * <ul>
 *   <li>{@code users} table → authentication identity (email, password, role)</li>
 *   <li>{@code doctors} table → professional profile (specialty, fee, city...)</li>
 * </ul>
 *
 * <h2>Relationship Design: {@code @OneToOne} with {@code FetchType.LAZY}</h2>
 *
 * <p>The {@code user} field uses {@code fetch = FetchType.LAZY}. This is critical:
 * <ul>
 *   <li>Without {@code LAZY}, every query that loads a Doctor will also issue a
 *       second SQL {@code SELECT} to fetch the User — even if you only need the
 *       doctor's specialty or city.</li>
 *   <li>With {@code optional = false}, Hibernate knows the association is always
 *       present. This allows it to use a proxy instead of hitting the DB to check
 *       for null, making {@code FetchType.LAZY} truly effective on the owning side.</li>
 * </ul>
 *
 * <p><b>Important Hibernate quirk</b>: For {@code @OneToOne}, lazy loading only
 * works reliably on the <b>owning side</b> (the entity that holds the FK column).
 * The {@code Doctor} entity is the owning side here because it contains the
 * {@code user_id} foreign key column. If {@code User} declared
 * {@code @OneToOne(mappedBy = "user")}, lazy loading on that inverse side
 * would NOT work without bytecode instrumentation (e.g., Hibernate's
 * {@code @LazyToOne(LazyToOneOption.NO_PROXY)} or Gradle build-time bytecode weaving).
 *
 * <h2>Why {@code BigDecimal} for {@code consultationFee}?</h2>
 * <p>Never use {@code double} or {@code float} for monetary values.
 * IEEE 754 floating-point cannot represent many decimal fractions exactly.
 * For example, {@code 0.1 + 0.2 = 0.30000000000000004} in floating-point arithmetic.
 * {@code BigDecimal} provides exact decimal arithmetic, which is a legal and
 * financial requirement for any monetary value. The DB column is declared as
 * {@code DECIMAL(10, 2)} — exact fixed-point storage matching BigDecimal.
 *
 * <h2>Why {@code Integer} (wrapper) for {@code experienceYears}?</h2>
 * <p>{@code @NotNull} is meaningless on a Java primitive type (e.g., {@code int}).
 * A primitive {@code int} can NEVER be null by definition — the annotation would
 * compile and run but never trigger. Using the wrapper {@code Integer} allows
 * {@code @NotNull} to actually validate that the client provided the field.
 *
 * <h2>Lombok Strategy</h2>
 * <p>Same as {@link User} — explicit individual annotations, NOT {@code @Data}.
 * {@code @ToString(exclude = "user")} is critical: if omitted, calling
 * {@code toString()} on a Doctor (e.g., in a log statement) would trigger a
 * lazy-load of the associated {@code User}, potentially causing a
 * {@code LazyInitializationException} outside a transaction.
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
        name = "doctors",
        uniqueConstraints = {
                /*
                 * One User can have at most one Doctor profile.
                 * Enforced at the DB level — prevents duplicate profile creation
                 * even if application-layer checks are bypassed.
                 */
                @UniqueConstraint(
                        name = "uk_doctors_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {
                /*
                 * idx_doctors_user_id: Supports JOIN between doctors and users tables.
                 * MySQL creates this automatically for FK columns, but naming it
                 * explicitly improves readability in EXPLAIN output and monitoring tools.
                 */
                @Index(name = "idx_doctors_user_id", columnList = "user_id"),

                /*
                 * idx_doctors_specialty: Supports the most common filter:
                 * "Show all Cardiologists" → WHERE specialty = 'CARDIOLOGY'
                 */
                @Index(name = "idx_doctors_specialty", columnList = "specialty"),

                /*
                 * idx_doctors_city: Supports location-based search:
                 * "Find doctors in Chennai" → WHERE city = 'Chennai'
                 */
                @Index(name = "idx_doctors_city", columnList = "city"),

                /*
                 * Composite index: Covers the most common combined query:
                 * "Find Cardiologists in Chennai" → WHERE specialty = ? AND city = ?
                 * MySQL uses the leftmost prefix — this index also serves
                 * specialty-only queries, making idx_doctors_specialty potentially
                 * redundant. Both are retained for explicitness.
                 */
                @Index(name = "idx_doctors_specialty_city", columnList = "specialty, city"),

                /*
                 * idx_doctors_active: Filters inactive doctors efficiently.
                 * Queries like "findBySpecialtyAndActiveTrue" use this index.
                 */
                @Index(name = "idx_doctors_active", columnList = "is_active")
        }
)
public class Doctor extends BaseEntity {

    // =========================================================================
    // RELATIONSHIPS
    // =========================================================================

    /**
     * The {@link User} account that owns this Doctor profile.
     *
     * <p><b>{@code @OneToOne}</b>: Declares a one-to-one relationship.
     * {@code fetch = FetchType.LAZY}: Load the User only when explicitly accessed
     * (e.g., {@code doctor.getUser().getEmail()}), not on every Doctor query.
     * {@code optional = false}: This Doctor always has an associated User.
     * Tells Hibernate to use a proxy instead of a null-check SELECT,
     * making lazy loading truly effective on this owning side.
     *
     * <p><b>{@code @JoinColumn}</b>: Declares that the {@code doctors} table
     * contains the FK column {@code user_id} that references {@code users(id)}.
     * {@code nullable = false}: A Doctor cannot exist without a User.
     * {@code foreignKey}: Names the FK constraint for DBA readability.
     */
    @NotNull(message = "Doctor must be associated with a user account")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_doctors_user_id")
    )
    private User user;

    // =========================================================================
    // PROFESSIONAL INFORMATION
    // =========================================================================

    /**
     * Medical specialty, stored as {@code VARCHAR} via {@code EnumType.STRING}.
     *
     * <p>{@code @NotNull}: Specialty is mandatory — a doctor without a specialty
     * cannot be meaningfully searched. Note: {@code @NotNull} (not {@code @NotBlank})
     * because this is an enum, not a String; enums cannot be blank.
     *
     * <p>{@code length = 30}: Long enough for the longest specialty name stored
     * (e.g., {@code "GASTROENTEROLOGY"} = 16 chars). 30 provides safe headroom.
     */
    @NotNull(message = "Specialty is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", nullable = false, length = 30)
    private Specialty specialty;

    /**
     * Academic and professional qualifications.
     *
     * <p>Examples: {@code "MBBS, MD (Cardiology)"}, {@code "BDS, MDS (Orthodontics)"}.
     *
     * <p>{@code length = 200}: Generous length for multi-degree qualifications.
     */
    @NotBlank(message = "Qualification is required")
    @Size(max = 200, message = "Qualification must not exceed 200 characters")
    @Column(name = "qualification", nullable = false, length = 200)
    private String qualification;

    /**
     * Number of years of professional medical experience.
     *
     * <p>Uses {@code Integer} (wrapper class) instead of {@code int} (primitive)
     * so that {@code @NotNull} is meaningful — a primitive int defaults to 0
     * and can never be null, making {@code @NotNull} a no-op on primitives.
     *
     * <p>{@code @Min(0)}: Experience cannot be negative.
     * {@code @Max(60)}: A practical upper bound — prevents data entry errors
     * (e.g., typo of {@code 600} instead of {@code 60}).
     */
    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 60, message = "Experience years cannot exceed 60")
    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    /**
     * Per-appointment consultation fee in INR.
     *
     * <p><b>ALWAYS use {@code BigDecimal} for monetary values.</b>
     * {@code double} and {@code float} use binary floating-point arithmetic
     * (IEEE 754) which cannot represent most decimal fractions exactly.
     * {@code BigDecimal} uses exact decimal arithmetic — mandatory for money.
     *
     * <p>{@code @DecimalMin("0.01")}: Fee must be greater than zero.
     * A free consultation (0.00) is a valid business concept but is modelled
     * as a separate field if needed — a required fee implies a paid service.
     *
     * <p>{@code @Digits(integer = 8, fraction = 2)}: Ensures the value fits
     * within the DB column precision {@code DECIMAL(10, 2)} —
     * up to 8 digits before and 2 digits after the decimal point.
     * Without this, a BigDecimal with more precision would cause a DB error
     * with no helpful client-facing message.
     *
     * <p>{@code precision = 10, scale = 2}: Mirrors the {@code DECIMAL(10,2)}
     * MySQL column — Hibernate uses this to generate the DDL correctly.
     */
    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.01", message = "Consultation fee must be greater than 0")
    @Digits(
            integer = 8,
            fraction = 2,
            message = "Consultation fee must have at most 8 integer digits and 2 decimal places"
    )
    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    // =========================================================================
    // CONTACT INFORMATION
    // =========================================================================

    /**
     * Doctor's contact phone number.
     *
     * <p>{@code @Pattern}: Validates the format using a regex that allows:
     * <ul>
     *   <li>Optional leading {@code +} (for international format, e.g., {@code +91})</li>
     *   <li>10–15 digits</li>
     *   <li>Common separators: spaces, hyphens, parentheses</li>
     * </ul>
     * Examples of valid values: {@code "9876543210"}, {@code "+91-98765-43210"},
     * {@code "+1 (555) 123-4567"}.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]{8,14}$",
            message = "Phone number must be a valid format (e.g., +91-9876543210)"
    )
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /**
     * City where the doctor practices.
     *
     * <p>Used for location-based search ({@code idx_doctors_city} index).
     * Stored as a plain string — not a FK to a cities table — to keep
     * the schema simple. Search uses {@code LIKE} or exact match.
     */
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    // =========================================================================
    // OPTIONAL PROFILE FIELDS
    // =========================================================================

    /**
     * Optional professional biography / about text.
     *
     * <p>Displayed on the doctor's profile page. Nullable — a doctor can be
     * registered without a bio and fill it in later.
     *
     * <p>{@code @Lob} is intentionally NOT used. MySQL maps {@code TEXT}
     * columns via {@code columnDefinition = "TEXT"}. Using {@code @Lob} with
     * MySQL triggers Hibernate to use {@code LONGTEXT} (4GB max) or
     * causes driver-level issues with some MySQL Connector/J versions.
     */
    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    /**
     * URL to the doctor's profile photo (stored on a CDN or cloud storage like AWS S3).
     *
     * <p>The entity stores only the URL string, never the binary image data.
     * Images are uploaded to S3/cloud storage in a separate file upload flow,
     * and the resulting URL is stored here.
     *
     * <p>Nullable — the profile image is optional; a default avatar is shown
     * in the UI when this is null.
     */
    @Size(max = 512, message = "Profile image URL must not exceed 512 characters")
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    // =========================================================================
    // STATUS
    // =========================================================================

    /**
     * Soft-delete flag. {@code true} = Doctor is accepting appointments.
     * {@code false} = Doctor is deactivated (not shown in search results).
     *
     * <p>Same soft-delete rationale as {@link User#active}: preserves referential
     * integrity with existing appointments and maintains the audit trail.
     *
     * <p>{@code @Builder.Default}: Without this, the {@code @Builder}-created Doctor
     * would default to {@code active = false} — silently hiding all new doctors
     * from the booking search. This is one of the most common and dangerous
     * bugs in Lombok + JPA projects.
     */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // =========================================================================
    // equals() and hashCode()
    // =========================================================================
    // Strategy: identity-based using database id.
    //
    // Unlike User (which used email as the business key), Doctor does not have
    // a single simple primitive business key accessible without loading the
    // related User proxy. Using the database id is safe here because:
    //   - We use null-safe id comparison for pre-persist (transient) state.
    //   - hashCode is constant to survive the transient → persistent transition.
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor other)) return false;
        // Only equal if both are persisted (non-null id) and ids match
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        // Constant across lifecycle: prevents Set/HashMap breakage when
        // the entity transitions from transient (id=null) to persistent.
        return getClass().hashCode();
    }
}
