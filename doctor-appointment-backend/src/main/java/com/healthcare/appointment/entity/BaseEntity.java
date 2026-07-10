package com.healthcare.appointment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base entity that provides common fields for ALL entities in the system.
 *
 * <h2>Design Decisions</h2>
 * <ol>
 *   <li><b>{@code @MappedSuperclass}</b>: Tells Hibernate that this class is NOT
 *       a standalone table. Its fields are mapped into each child entity's table.
 *       Every child entity owns the columns — no join is ever required to read them.</li>
 *
 *   <li><b>{@code @EntityListeners(AuditingEntityListener.class)}</b>: Registers
 *       Spring Data's auditing listener on this class. This listener intercepts
 *       {@code @PrePersist} and {@code @PreUpdate} JPA lifecycle events and
 *       populates {@code @CreatedDate}, {@code @LastModifiedDate}, {@code @CreatedBy},
 *       and {@code @LastModifiedBy} fields automatically.</li>
 *
 *   <li><b>ID Strategy — {@code GenerationType.IDENTITY}</b>: Delegates ID generation
 *       to the MySQL AUTO_INCREMENT column. This is the most efficient strategy
 *       for MySQL because it does NOT require a separate sequence table or a
 *       pre-select query. Note: {@code SEQUENCE} is better for PostgreSQL/Oracle.</li>
 *
 *   <li><b>{@code LocalDateTime} over {@code Date}</b>: Java 8+ date-time API is
 *       immutable and timezone-aware. {@code java.util.Date} is mutable and legacy.
 *       Hibernate 6 natively supports {@code LocalDateTime} without extra converters.</li>
 *
 *   <li><b>{@code @Column(updatable = false)} on {@code createdAt} and {@code createdBy}</b>:
 *       Prevents Hibernate from including these columns in UPDATE statements.
 *       Once set on INSERT, they must never change — enforced at the ORM level.</li>
 *
 *   <li><b>Implements {@code Serializable}</b>: Required if entities are stored in
 *       a distributed cache (Redis), used in HTTP sessions, or transferred between
 *       JVMs. Declaring it at the base level ensures all child entities comply.</li>
 *
 *   <li><b>{@code @Getter} / {@code @Setter} (not {@code @Data})</b>: {@code @Data}
 *       generates {@code equals()} and {@code hashCode()} based on all fields, which
 *       is dangerous for JPA entities (proxied objects cause equality issues).
 *       Entities should use ID-based equality. We'll override in child classes.</li>
 * </ol>
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // =========================================================================
    // PRIMARY KEY
    // =========================================================================

    /**
     * Auto-incremented primary key.
     *
     * <p>{@code GenerationType.IDENTITY} relies on MySQL's AUTO_INCREMENT.
     * Hibernate submits the INSERT first, then reads the generated key back
     * via {@code Statement.getGeneratedKeys()} — no extra round-trip needed.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    // =========================================================================
    // AUDIT FIELDS — TIMESTAMPS
    // =========================================================================

    /**
     * Timestamp when the record was first persisted.
     *
     * <p>{@code @CreatedDate}: Spring Data populates this field on the first
     * {@code save()} call (pre-persist lifecycle event).
     *
     * <p>{@code nullable = false}: Every record must have a creation timestamp.
     *
     * <p>{@code updatable = false}: Hibernate excludes this column from all
     * UPDATE SQL statements — creation time is immutable by design.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the most recent update to this record.
     *
     * <p>{@code @LastModifiedDate}: Spring Data updates this on every
     * {@code save()} call after the initial persist (pre-update lifecycle event).
     *
     * <p>{@code nullable = false}: A new record's last modified time is the
     * same as its created time on first persist, so it is always populated.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =========================================================================
    // AUDIT FIELDS — ACTORS
    // =========================================================================

    /**
     * Username (email) of the user who created this record.
     *
     * <p>Value is resolved by {@link com.healthcare.appointment.config.AuditorAwareImpl}
     * which reads the authenticated principal from the Spring Security context.
     * Falls back to {@code "SYSTEM"} for unauthenticated operations (seeding, migration).
     *
     * <p>{@code length = 100}: Matches the email column length in the users table.
     *
     * <p>{@code updatable = false}: Creator is fixed at insert time — immutable.
     */
    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * Username (email) of the user who last modified this record.
     *
     * <p>Value is resolved by {@link com.healthcare.appointment.config.AuditorAwareImpl}
     * on every save/update operation.
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
