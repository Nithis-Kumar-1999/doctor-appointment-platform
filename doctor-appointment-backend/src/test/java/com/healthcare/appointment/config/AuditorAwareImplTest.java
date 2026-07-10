package com.healthcare.appointment.config;

import com.healthcare.appointment.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuditorAwareImpl}.
 *
 * <p><b>Testing Strategy:</b>
 * <ul>
 *   <li>Uses {@code @ExtendWith(MockitoExtension.class)} — lightweight, no Spring context needed.</li>
 *   <li>Mocks {@code SecurityContext} and {@code Authentication} — tests ONLY the auditor logic,
 *       not Spring Security internals.</li>
 *   <li>{@code SecurityContextHolder} is reset after each test to prevent thread-local pollution
 *       between test runs.</li>
 *   <li>Uses AssertJ for fluent, readable assertions.</li>
 *   <li>Test method naming convention: {@code methodName_givenCondition_shouldExpectedBehavior}</li>
 * </ul>
 *
 * @author Nithish Kumar
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditorAwareImpl Unit Tests")
class AuditorAwareImplTest {

    /** The class under test — Mockito injects mocks into its constructor (no mocks here, direct instantiation). */
    @InjectMocks
    private AuditorAwareImpl auditorAwareImpl;

    /** Mocked SecurityContext — returned from SecurityContextHolder. */
    @Mock
    private SecurityContext securityContext;

    /** Mocked Authentication — returned from the mocked SecurityContext. */
    @Mock
    private Authentication authentication;

    // =========================================================================
    // SETUP & TEARDOWN
    // =========================================================================

    @BeforeEach
    void setUp() {
        // Replace the static SecurityContextHolder with our mocked context
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // CRITICAL: Clear the SecurityContext after every test.
        // SecurityContextHolder uses ThreadLocal — if not cleared, the mocked context
        // leaks into the next test running on the same thread.
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // TEST CASES
    // =========================================================================

    @Test
    @DisplayName("Given no authentication in context, should return SYSTEM")
    void getCurrentAuditor_givenNoAuthentication_shouldReturnSystem() {
        // GIVEN: SecurityContext returns null authentication (no active session)
        when(securityContext.getAuthentication()).thenReturn(null);

        // WHEN
        Optional<String> auditor = auditorAwareImpl.getCurrentAuditor();

        // THEN
        assertThat(auditor)
                .isPresent()
                .hasValue(AppConstants.SYSTEM_AUDITOR);

        // Verify the SecurityContext was queried exactly once
        verify(securityContext, times(1)).getAuthentication();
        // Verify that authentication mock was NEVER called (it was null)
        verifyNoInteractions(authentication);
    }

    @Test
    @DisplayName("Given anonymous/unauthenticated principal, should return SYSTEM")
    void getCurrentAuditor_givenNotAuthenticated_shouldReturnSystem() {
        // GIVEN: Authentication exists but isAuthenticated() returns false
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // WHEN
        Optional<String> auditor = auditorAwareImpl.getCurrentAuditor();

        // THEN
        assertThat(auditor)
                .isPresent()
                .hasValue(AppConstants.SYSTEM_AUDITOR);

        verify(authentication, times(1)).isAuthenticated();
        // getName() should never be called if isAuthenticated() is false
        verify(authentication, never()).getName();
    }

    @Test
    @DisplayName("Given authenticated user with blank name, should return SYSTEM")
    void getCurrentAuditor_givenAuthenticatedWithBlankName_shouldReturnSystem() {
        // GIVEN: Authenticated but principal name is blank (defensive guard test)
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("  "); // blank string

        // WHEN
        Optional<String> auditor = auditorAwareImpl.getCurrentAuditor();

        // THEN
        assertThat(auditor)
                .isPresent()
                .hasValue(AppConstants.SYSTEM_AUDITOR);
    }

    @Test
    @DisplayName("Given fully authenticated user, should return user's email")
    void getCurrentAuditor_givenAuthenticatedUser_shouldReturnUserEmail() {
        // GIVEN: Valid authenticated user with an email as principal
        String userEmail = "doctor@healthcare.com";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(userEmail);

        // WHEN
        Optional<String> auditor = auditorAwareImpl.getCurrentAuditor();

        // THEN
        assertThat(auditor)
                .isPresent()
                .hasValue(userEmail);

        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("Given authenticated admin user, should return admin's email")
    void getCurrentAuditor_givenAdminUser_shouldReturnAdminEmail() {
        // GIVEN: Admin user authenticated
        String adminEmail = "admin@healthcare.com";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(adminEmail);

        // WHEN
        Optional<String> auditor = auditorAwareImpl.getCurrentAuditor();

        // THEN
        assertThat(auditor)
                .isPresent()
                .hasValue(adminEmail);
    }

    @Test
    @DisplayName("Return value should always be non-empty Optional (never empty Optional)")
    void getCurrentAuditor_shouldAlwaysReturnNonEmptyOptional() {
        // GIVEN: No authentication (worst case)
        when(securityContext.getAuthentication()).thenReturn(null);

        // WHEN
        Optional<String> auditor = auditorAwareImpl.getCurrentAuditor();

        // THEN: We guarantee a non-empty Optional in all scenarios
        // Spring Data skips populating @CreatedBy/@LastModifiedBy if Optional is empty.
        // Our implementation always provides a value — either user email or "SYSTEM".
        assertThat(auditor).isPresent();
        assertThat(auditor.get()).isNotBlank();
    }
}
