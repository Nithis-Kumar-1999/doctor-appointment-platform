package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.Appointment;
import com.healthcare.appointment.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for {@link Appointment} entity.
 *
 * @author Nithish Kumar
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Returns a paginated list of appointments for the given Patient.
     *
     * <p><b>Primary use</b>: Patient dashboard — "My Appointments" page
     * with sorting (e.g., by date descending).
     *
     * @param patientId the id of the Patient
     * @param pageable  pagination and sorting
     * @return a page of the patient's appointments
     */
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    /**
     * Returns a paginated list of appointments for the given Doctor.
     *
     * <p><b>Primary use</b>: Doctor dashboard — "My Appointments" page.
     *
     * @param doctorId the id of the Doctor
     * @param pageable pagination and sorting
     * @return a page of the doctor's appointments
     */
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    /**
     * Returns a paginated list of appointments filtered by Patient and status.
     *
     * <p><b>Primary use</b>: Patient dashboard status filter tab —
     * "Show only my PENDING appointments" or "Show my COMPLETED appointments."
     *
     * @param patientId the id of the Patient
     * @param status    the appointment status to filter by
     * @param pageable  pagination and sorting
     * @return a page of matching appointments
     */
    Page<Appointment> findByPatientIdAndStatus(
            Long patientId, AppointmentStatus status, Pageable pageable
    );

    /**
     * Returns a paginated list of appointments filtered by Doctor and status.
     *
     * <p><b>Primary use</b>: Doctor dashboard status filter tab —
     * "Show only my CONFIRMED appointments for today."
     *
     * @param doctorId the id of the Doctor
     * @param status   the appointment status to filter by
     * @param pageable pagination and sorting
     * @return a page of matching appointments
     */
    Page<Appointment> findByDoctorIdAndStatus(
            Long doctorId, AppointmentStatus status, Pageable pageable
    );

    /**
     * Returns a paginated list of all appointments with the given status.
     *
     * <p><b>Primary use</b>: Admin dashboard — "Show all PENDING appointments
     * awaiting confirmation."
     *
     * @param status   the status to filter by
     * @param pageable pagination and sorting
     * @return a page of matching appointments
     */
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    /**
     * Returns all appointments booked with the given Doctor on the given date.
     *
     * <p><b>Primary use</b>: {@code AppointmentService.getAvailableSlots()} —
     * retrieves all booked times for a doctor on a specific date so the service
     * can subtract them from the doctor's availability window to compute the
     * remaining open slots shown to the patient.
     *
     * <p>Returns {@code List} because all slots for a single day are needed
     * at once for the subtraction logic — pagination is not useful here.
     *
     * @param doctorId        the id of the Doctor
     * @param appointmentDate the date to query
     * @return a list of appointments on that date (may be empty)
     */
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    /**
     * Checks whether a specific date-time slot is already booked for a Doctor.
     *
     * <p><b>Primary use</b>: {@code AppointmentService.bookAppointment()} —
     * last-line-of-defence check in the service layer before saving.
     * The DB unique constraint on {@code (doctor_id, appointment_date, appointment_time)}
     * is the ultimate guard, but this check provides an early, user-friendly
     * conflict error instead of catching a {@code DataIntegrityViolationException}.
     *
     * <p>Generates an efficient {@code SELECT 1 ... LIMIT 1} or {@code COUNT(1)}
     * query — does not load the full Appointment entity.
     *
     * @param doctorId        the id of the Doctor
     * @param appointmentDate the date of the slot
     * @param appointmentTime the time of the slot
     * @return true if the slot is already booked
     */
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime
    );

    /**
     * Counts appointments for a Doctor on a given date, excluding cancelled ones.
     *
     * <p><b>Primary use</b>: Optional optimisation for the admin dashboard to
     * show "X / Y slots filled" for a doctor on a given day without loading
     * the full list of appointments.
     *
     * <p>{@code @Query} is used here because the derived query name would be
     * excessively long:
     * {@code countByDoctorIdAndAppointmentDateAndStatusNot} — while valid,
     * a named JPQL query is more readable for this case.
     *
     * @param doctorId        the id of the Doctor
     * @param appointmentDate the date to count for
     * @param status          the status to exclude (typically CANCELLED)
     * @return the count of active (non-cancelled) appointments
     */
    @Query("""
            SELECT COUNT(a) FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
              AND a.status <> :excludedStatus
            """)
    long countActiveAppointmentsForDoctorOnDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate appointmentDate,
            @Param("excludedStatus") AppointmentStatus excludedStatus
    );
}
