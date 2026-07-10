package com.healthcare.appointment.util;

/**
 * Application-wide constants.
 *
 * <p>Design Decisions:
 * <ul>
 *   <li>Final class with a private constructor — prevents instantiation.
 *       This is the standard utility class pattern (Item 4, Effective Java).</li>
 *   <li>All fields are {@code public static final} — accessed without instantiation.</li>
 *   <li>Only constants that are genuinely needed across the application today are declared.
 *       Do NOT add speculative constants here.</li>
 * </ul>
 *
 * @author Nithish Kumar
 * @version 1.0.0
 */
public final class AppConstants {

    // =========================================================================
    // PRIVATE CONSTRUCTOR
    // Prevents instantiation: AppConstants should never be "new AppConstants()"
    // =========================================================================
    private AppConstants() {
        throw new UnsupportedOperationException(
                "AppConstants is a utility class and cannot be instantiated."
        );
    }

    // =========================================================================
    // API VERSIONING
    // =========================================================================

    /** Base path for all API endpoints. Matches application context in yml. */
    public static final String API_BASE_PATH = "/api/v1";

    // =========================================================================
    // PAGINATION DEFAULTS
    // These mirror the values in application.yml > application.pagination
    // Controllers use these as @RequestParam defaults so Swagger shows them.
    // =========================================================================

    /** Default page number for paginated API responses (0-indexed). */
    public static final String DEFAULT_PAGE_NUMBER = "0";

    /** Default number of records per page. */
    public static final String DEFAULT_PAGE_SIZE   = "10";

    /** Default field to sort paginated results by. */
    public static final String DEFAULT_SORT_BY     = "createdAt";

    /** Default sort direction for paginated results. */
    public static final String DEFAULT_SORT_DIR    = "desc";

    // =========================================================================
    // AUDITING
    // Used by AuditorAwareImpl when no authenticated user is present.
    // Covers: data seeding, migration scripts, system-initiated events.
    // =========================================================================

    /** Fallback auditor name when no authenticated user exists in the context. */
    public static final String SYSTEM_AUDITOR = "SYSTEM";

    // =========================================================================
    // SECURITY
    // =========================================================================

    /** HTTP header name that carries the JWT token. */
    public static final String AUTH_HEADER_NAME   = "Authorization";

    /** Prefix expected before the JWT token value in the Authorization header. */
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
}
