package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link Patient} entity.
 *
 * @author Nithish Kumar
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds the Patient profile associated with the given User id.
     *
     * <p><b>Primary use</b>: After a Patient logs in, the service fetches their
     * profile using the authenticated User's id from the JWT token.
     * Also used in {@code AppointmentService.bookAppointment()} to resolve
     * the Patient entity from the authenticated user's context.
     *
     * @param userId the id of the associated User
     * @return an Optional containing the Patient profile, or empty if not found
     */
    Optional<Patient> findByUserId(Long userId);

    /**
     * Checks whether a Patient profile already exists for the given User id.
     *
     * <p><b>Primary use</b>: {@code PatientService.createProfile()} — prevents
     * creating a duplicate Patient profile for the same User account.
     *
     * @param userId the id of the associated User
     * @return true if a Patient profile exists for this user
     */
    boolean existsByUserId(Long userId);
}
