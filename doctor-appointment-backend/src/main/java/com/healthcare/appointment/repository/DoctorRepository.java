package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.enums.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link Doctor} entity.
 *
 * @author Nithish Kumar
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Finds the Doctor profile associated with the given User id.
     *
     * <p><b>Primary use</b>: After a Doctor logs in, the service fetches their
     * profile using the authenticated User's id from the JWT token.
     *
     * <p>Spring Data JPA resolves {@code findByUserId} by navigating the
     * {@code Doctor.user} relationship and matching on {@code user.id}.
     * No JOIN is written manually — this is property traversal via the {@code _}
     * separator convention ({@code user_id} → {@code user.id}).
     *
     * @param userId the id of the associated User
     * @return an Optional containing the Doctor profile, or empty if not found
     */
    Optional<Doctor> findByUserId(Long userId);

    /**
     * Checks whether a Doctor profile already exists for the given User id.
     *
     * <p><b>Primary use</b>: {@code DoctorService.createProfile()} — prevents
     * creating a duplicate Doctor profile for the same User account.
     *
     * @param userId the id of the associated User
     * @return true if a Doctor profile exists for this user
     */
    boolean existsByUserId(Long userId);

    /**
     * Returns a paginated list of all active doctors.
     *
     * <p><b>Primary use</b>: Patient-facing doctor listing page — shows all
     * available doctors when no specialty or city filter is applied.
     *
     * @param pageable pagination and sorting parameters
     * @return a page of active Doctor records
     */
    Page<Doctor> findAllByActiveTrue(Pageable pageable);

    /**
     * Returns a paginated list of active doctors with the given specialty.
     *
     * <p><b>Primary use</b>: {@code DoctorService.searchDoctors()} when a
     * patient filters by specialty only (no city specified).
     *
     * @param specialty the medical specialty to filter by
     * @param pageable  pagination and sorting parameters
     * @return a page of active Doctors with the given specialty
     */
    Page<Doctor> findBySpecialtyAndActiveTrue(Specialty specialty, Pageable pageable);

    /**
     * Returns a paginated list of active doctors in the given city.
     *
     * <p><b>Primary use</b>: {@code DoctorService.searchDoctors()} when a
     * patient filters by city only (no specialty specified).
     *
     * <p>{@code IgnoreCase} ensures "Chennai", "chennai", "CHENNAI" all match,
     * avoiding user-input casing issues without extra normalisation in the service.
     *
     * @param city     the city to filter by (case-insensitive)
     * @param pageable pagination and sorting parameters
     * @return a page of active Doctors in the given city
     */
    Page<Doctor> findByCityIgnoreCaseAndActiveTrue(String city, Pageable pageable);

    /**
     * Returns a paginated list of active doctors filtered by both specialty and city.
     *
     * <p><b>Primary use</b>: {@code DoctorService.searchDoctors()} when a
     * patient applies both specialty and city filters simultaneously.
     * This is the most common combined search in a doctor-booking application.
     *
     * @param specialty the medical specialty to filter by
     * @param city      the city to filter by (case-insensitive)
     * @param pageable  pagination and sorting parameters
     * @return a page of active Doctors matching both criteria
     */
    Page<Doctor> findBySpecialtyAndCityIgnoreCaseAndActiveTrue(
            Specialty specialty, String city, Pageable pageable
    );
}
