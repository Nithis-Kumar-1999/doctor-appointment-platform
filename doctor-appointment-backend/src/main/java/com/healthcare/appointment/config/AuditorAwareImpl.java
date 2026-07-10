package com.healthcare.appointment.config;

import com.healthcare.appointment.util.AppConstants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Spring Data JPA Auditing provider.
 *
 * <p>This class answers one question that Spring Data's auditing infrastructure asks
 * on every {@code save()} or {@code update()} call:
 * <blockquote><i>"Who is performing this operation right now?"</i></blockquote>
 *
 * <p>The returned value is stored in the {@code @CreatedBy} and {@code @LastModifiedBy}
 * columns of every entity that extends {@link com.healthcare.appointment.entity.BaseEntity}.
 *
 * <h2>Why {@code @Component("auditorAwareImpl")}?</h2>
 * <p>The bean name {@code "auditorAwareImpl"} must EXACTLY match the
 * {@code auditorAwareRef} attribute in the {@code @EnableJpaAuditing} annotation
 * declared on {@code DoctorAppointmentApplication}. A mismatch causes a
 * {@code NoSuchBeanDefinitionException} at startup.
 *
 * <h2>Why does this return {@code Optional<String>}?</h2>
 * <p>The {@code AuditorAware<T>} contract returns {@code Optional<T>} to signal
 * that an auditor might not always be identifiable — e.g., during data seeding,
 * scheduled jobs, or before a user authenticates. Spring Data interprets an
 * empty Optional as "skip populating this field."
 *
 * <h2>Security Context Thread-Safety</h2>
 * <p>{@code SecurityContextHolder} uses a {@code ThreadLocal} by default
 * (MODE_THREADLOCAL), meaning the security context is isolated per HTTP request
 * thread. This is safe for standard synchronous Spring MVC applications.
 * If you use virtual threads (Java 21+) or reactive programming (WebFlux),
 * the mode must be changed to {@code MODE_INHERITABLETHREADLOCAL}.
 *
 * <h2>Fallback Strategy</h2>
 * <p>If no authentication exists in the context (null, anonymous, or not
 * authenticated), the method returns {@code "SYSTEM"} — not an empty Optional.
 * This ensures that system-initiated operations (e.g., database migrations,
 * scheduled batch jobs) are still traceable in audit columns.
 *
 * @author Nithish Kumar
 * @version 1.0.0
 * @see org.springframework.data.domain.AuditorAware
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Resolves the current auditor from the Spring Security context.
     *
     * <p>Resolution logic (in priority order):
     * <ol>
     *   <li>Read {@code Authentication} from {@code SecurityContextHolder}.</li>
     *   <li>If {@code Authentication} is null → return {@code "SYSTEM"} (no active session).</li>
     *   <li>If not authenticated (e.g., anonymous user) → return {@code "SYSTEM"}.</li>
     *   <li>If principal name is blank → return {@code "SYSTEM"} (defensive guard).</li>
     *   <li>Otherwise → return the principal name (which is the user's email).</li>
     * </ol>
     *
     * @return an {@code Optional} containing the current user's email, or "SYSTEM"
     */
    @Override
    public Optional<String> getCurrentAuditor() {

        // Step 1: Read the authentication object from the current thread's security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Step 2: Guard — no authentication in context at all
        // This happens during: application startup, data seeding, unauthenticated requests
        if (authentication == null) {
            return Optional.of(AppConstants.SYSTEM_AUDITOR);
        }

        // Step 3: Guard — authentication object exists but the user is not yet authenticated
        // Examples: anonymous user, pre-authentication state, expired session
        if (!authentication.isAuthenticated()) {
            return Optional.of(AppConstants.SYSTEM_AUDITOR);
        }

        // Step 4: Guard — principal name is blank (shouldn't happen, but defensive programming)
        // authentication.getName() returns the username (email in our case) set by
        // UserDetailsService.loadUserByUsername()
        String principalName = authentication.getName();
        if (principalName == null || principalName.isBlank()) {
            return Optional.of(AppConstants.SYSTEM_AUDITOR);
        }

        // Step 5: Happy path — return the authenticated user's identifier (email)
        return Optional.of(principalName);
    }
}
