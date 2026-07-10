package com.healthcare.appointment.entity;

import com.healthcare.appointment.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * JPA Entity representing an authenticated user of the system.
 *
 * <p>This entity is the <b>single source of authentication identity</b> for all roles.
 * Whether a user is an ADMIN, DOCTOR, or PATIENT, they all have exactly one row
 * in the {@code users} table. Role-specific profile data (e.g., specialty for a
 * Doctor, or blood group for a Patient) is stored in separate extension tables
 * ({@code doctors}, {@code patients}) with a 1-to-1 FK back to this entity.
 *
 * <h2>Lombok Annotation Strategy — Why NOT {@code @Data}?</h2>
 * <p>{@code @Data} is a convenience annotation that bundles:
 * {@code @Getter}, {@code @Setter}, {@code @RequiredArgsConstructor},
 * {@code @ToString}, and {@code @EqualsAndHashCode}.
 *
 * <p>For JPA entities, {@code @Data} causes two critical problems:
 * <ol>
 *   <li><b>{@code equals()}/{@code hashCode()} on all fields</b>: Hibernate loads
 *       entities as proxies (subclasses). Calling {@code equals()} on a proxy
 *       triggers lazy initialization. {@code @Data}'s generated {@code equals}
 *       compares ALL fields, meaning it can cause N+1 queries, LazyInitException,
 *       and incorrect collection behavior in Sets and Maps.</li>
 *   <li><b>{@code @ToString} on all fields</b>: If any relationship is lazy
 *       (e.g., a future {@code @OneToMany}), calling {@code toString()} (e.g.,
 *       in a log statement) triggers lazy loading outside a transaction — causing
 *       {@code LazyInitializationException}.</li>
 * </ol>
 *
 * <p>Instead, we use individual Lombok annotations with explicit, intentional control:
 * <ul>
 *   <li>{@code @Getter} — safe, generates simple field accessors</li>
 *   <li>{@code @Setter} — safe for plain fields</li>
 *   <li>{@code @Builder} — clean object construction without long constructor chains</li>
 *   <li>{@code @NoArgsConstructor} — required by JPA specification</li>
 *   <li>{@code @AllArgsConstructor} — required by {@code @Builder} with {@code @NoArgsConstructor}</li>
 *   <li>{@code @ToString} — with {@code exclude = "password"} to prevent credential leaks in logs</li>
 * </ul>
 * <p>{@code equals()} and {@code hashCode()} are overridden manually using only
 * the stable, unique {@code email} business key — never the mutable or lazy fields.
 *
 * <h2>isActive Field — Naming Convention Note</h2>
 * <p>The Java field is named {@code active} (not {@code isActive}).
 * The database column is explicitly mapped to {@code is_active} via {@code @Column(name = "is_active")}.
 * This is intentional: Lombok generates a getter named {@code isActive()} for a
 * {@code boolean} field named {@code active}, following the JavaBeans convention.
 * If the field were named {@code isActive}, Lombok would still generate {@code isActive()}
 * but the setter would become the awkward {@code setIsActive()} — a JavaBeans violation.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
@Entity

/*
 * @Table defines the actual MySQL table name and declares:
 *   - uniqueConstraints: Enforces email uniqueness at the DATABASE level (not just JPA).
 *     This creates a MySQL UNIQUE INDEX on the email column.
 *     JPA would enforce uniqueness via the application layer only without this.
 *   - indexes: Creates an explicit B-Tree INDEX on email for fast login lookups.
 *     Even though the UNIQUE constraint already creates an index, declaring it
 *     explicitly here documents the intent and allows naming the index for
 *     easier identification in MySQL EXPLAIN plans and monitoring tools.
 */
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",     // Named constraint for easy identification in DB
                        columnNames = "email"
                )
        },
        indexes = {
                @Index(
                        name = "idx_users_email",    // Named index for query plan readability
                        columnList = "email"
                ),
                @Index(
                        name = "idx_users_role",     // Supports filtered queries: findAllByRole(DOCTOR)
                        columnList = "role"
                )
        }
)
public class User extends BaseEntity {

    // =========================================================================
    // PERSONAL INFORMATION
    // =========================================================================

    /**
     * User's first name.
     *
     * <p>{@code @NotBlank}: Fails validation if null, empty, or whitespace-only.
     * Stronger than {@code @NotNull} (which permits "") and {@code @NotEmpty}
     * (which permits "   ").
     *
     * <p>{@code @Size(max = 50)}: Mirrors the VARCHAR(50) column definition.
     * Without this, a client could POST a 10,000-character first name and
     * Hibernate would throw a silent truncation error or a DB exception
     * with no helpful client-facing message.
     *
     * <p>{@code nullable = false}: Enforces NOT NULL at the database DDL level.
     * The JPA constraint ({@code @NotBlank}) validates at the application layer;
     * the DB constraint is the final safety net.
     */
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * User's last name.
     *
     * <p>Same constraint rationale as {@code firstName}.
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // =========================================================================
    // AUTHENTICATION CREDENTIALS
    // =========================================================================

    /**
     * User's email address — used as the login identifier (username).
     *
     * <p>{@code @Email}: Validates RFC 5322 email format at the application layer.
     * Note: This does NOT send a verification email — it only checks the format.
     *
     * <p>{@code @NotBlank}: Email cannot be null or blank.
     *
     * <p>{@code unique = true}: Alternative to declaring {@code @UniqueConstraint}
     * in {@code @Table}. We use both: {@code @Table} uniqueConstraints gives us a
     * named constraint for DB administration; this is redundant but self-documenting.
     *
     * <p>{@code length = 100}: RFC 5321 specifies email addresses can be up to
     * 254 characters, but 100 is a pragmatic industry standard that covers 99.9%
     * of real-world email addresses while keeping the index compact.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * BCrypt-hashed password.
     *
     * <p><b>CRITICAL SECURITY RULE</b>: This field stores the BCrypt hash,
     * NEVER the plain-text password. Plain-text passwords are received in DTOs,
     * hashed in the AuthService via {@code BCryptPasswordEncoder.encode()},
     * and then stored here.
     *
     * <p>{@code length = 255}: BCrypt always produces a 60-character hash,
     * but 255 provides flexibility if the hashing algorithm is ever upgraded
     * (e.g., Argon2, scrypt) without requiring a DB migration.
     *
     * <p>No {@code @NotBlank} here intentionally: Validation of the raw password
     * format (e.g., minimum 8 chars, complexity rules) belongs on the
     * {@code RegisterRequest} DTO — NOT on the entity, which stores the hash.
     *
     * <p>{@code @ToString(exclude = "password")} on the class prevents this
     * field from ever appearing in log output.
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // =========================================================================
    // ROLE & STATUS
    // =========================================================================

    /**
     * The user's application role, stored as a VARCHAR string in MySQL.
     *
     * <p>{@code @Enumerated(EnumType.STRING)}: Stores {@code "ADMIN"},
     * {@code "DOCTOR"}, or {@code "PATIENT"} as text.
     *
     * <p><b>Never use {@code EnumType.ORDINAL}</b>: Ordinal stores 0, 1, 2.
     * If a new role is inserted between existing ones (e.g., SUPER_ADMIN before
     * ADMIN), all existing ordinal values shift — silently corrupting the data
     * of every existing user. EnumType.STRING is always the safe choice.
     *
     * <p>{@code nullable = false}: Every user must have a role.
     * An un-roled user cannot be authorized to access any endpoint.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * Soft-delete flag. {@code true} = account is active; {@code false} = deactivated.
     *
     * <p>We use <b>soft delete</b> instead of hard delete ({@code DELETE FROM users})
     * for two reasons:
     * <ol>
     *   <li><b>Referential integrity</b>: A deleted user may have existing appointments,
     *       audit records, etc. Hard-deleting the user row would either violate FK
     *       constraints or cascade-delete related data — both are dangerous.</li>
     *   <li><b>Auditability</b>: Regulations (HIPAA, GDPR) often require that records
     *       of past medical interactions are retained even after a user leaves.</li>
     * </ol>
     *
     * <p>{@code @Builder.Default}: Required because {@code @Builder} does NOT honour
     * Java field initializer values ({@code = true}). Without {@code @Builder.Default},
     * the {@code active} field would be {@code false} when using the builder pattern,
     * which would create every new user as deactivated — a silent, catastrophic bug.
     *
     * <p>Field name is {@code active}; column is {@code is_active}.
     * Lombok generates getter {@code isActive()} and setter {@code setActive()}
     * following JavaBeans convention for boolean properties.
     */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // =========================================================================
    // equals() and hashCode()
    // =========================================================================
    // JPA entities must NOT use @Data or @EqualsAndHashCode on all fields.
    // We use the email as the business key for equality — it is:
    //   1. Unique (enforced by DB constraint)
    //   2. Immutable in practice (users don't change emails)
    //   3. Available before the entity is persisted (no null id issue)
    //
    // hashCode() returns a constant for the class — this ensures that an entity's
    // hash bucket never changes between "before persist" (id=null) and "after persist"
    // (id=some value), which would break Set/HashMap behavior for JPA entities.
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Use instanceof with pattern variable (Java 16+)
        // Also handles Hibernate proxy subclasses correctly (unlike getClass() == o.getClass())
        if (!(o instanceof User other)) return false;
        // email is our business key — unique and stable
        return email != null && email.equalsIgnoreCase(other.email);
    }

    @Override
    public int hashCode() {
        // Constant hash ensures the entity can safely move between
        // "transient" (null id) and "persistent" (non-null id) states
        // inside collections (Set, HashMap) without breaking lookup behavior.
        // Performance trade-off: all users fall into the same hash bucket,
        // but this is acceptable because large collections of User entities
        // are uncommon in application-layer code (they're paginated from DB).
        return getClass().hashCode();
    }
}
