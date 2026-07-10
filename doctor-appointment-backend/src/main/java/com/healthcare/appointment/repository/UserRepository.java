package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link User} entity.
 *
 * <p>All standard CRUD operations (findById, save, delete, findAll...) are
 * inherited from {@link JpaRepository} and require no re-declaration.
 *
 * @author Nithish Kumar
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * <p><b>Primary use</b>: {@code UserDetailsService.loadUserByUsername()} during
     * Spring Security authentication. Email is the login identifier in this system.
     *
     * <p>Returns {@code Optional} because no user with this email is a valid
     * (unauthenticated) scenario — the caller decides how to handle the absent case.
     *
     * @param email the user's email (case-sensitive — stored exactly as registered)
     * @return an Optional containing the User, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email already exists.
     *
     * <p><b>Primary use</b>: {@code AuthService.register()} — validates that
     * the email is not already taken before creating a new account.
     *
     * <p>Prefer this over {@code findByEmail(...).isPresent()} because it
     * generates a more efficient {@code SELECT COUNT(1)} or {@code SELECT 1}
     * query instead of loading the full entity just to check existence.
     *
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Returns a paginated list of users filtered by role.
     *
     * <p><b>Primary use</b>: Admin dashboard — "List all Doctors" or
     * "List all Patients" with pagination and sorting.
     *
     * @param role     the role to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of users with the given role
     */
    Page<User> findByRole(Role role, Pageable pageable);
}
