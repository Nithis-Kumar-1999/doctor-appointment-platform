package com.healthcare.appointment.repository;

import com.healthcare.appointment.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link DoctorAvailability} entity.
 *
 * @author Nithish Kumar
 */
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    /**
     * Returns all active availability entries for the given Doctor.
     *
     * <p><b>Primary use</b>: {@code DoctorService.getMySchedule()} and the
     * patient-facing "View Doctor's Weekly Schedule" page.
     * Only active entries are shown — inactive days are not bookable.
     *
     * <p>Returns {@code List} (not {@code Page}) because a doctor has at most
     * 7 availability entries (one per day of the week) — pagination is not needed
     * for a result set that is always ≤ 7 records.
     *
     * @param doctorId the id of the Doctor
     * @return a list of active DoctorAvailability entries
     */
    List<DoctorAvailability> findByDoctorIdAndActiveTrue(Long doctorId);

    /**
     * Returns the availability entry for a specific Doctor on a specific day.
     *
     * <p><b>Primary use</b>: {@code AppointmentService.bookAppointment()} —
     * before booking, the service checks that the Doctor is available on the
     * requested day and within the defined time window.
     *
     * <p>The unique constraint {@code (doctor_id, day_of_week)} on the table
     * guarantees this returns at most one record, making {@code Optional}
     * the correct return type.
     *
     * @param doctorId  the id of the Doctor
     * @param dayOfWeek the day of the week to look up
     * @return an Optional containing the availability entry, or empty if the
     *         Doctor has no schedule defined for this day
     */
    Optional<DoctorAvailability> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    /**
     * Checks whether an availability entry exists for a Doctor on the given day.
     *
     * <p><b>Primary use</b>: {@code DoctorService.createAvailability()} —
     * prevents creating a duplicate availability entry for a day that already
     * has one. More efficient than {@code findByDoctorIdAndDayOfWeek(...).isPresent()}.
     *
     * @param doctorId  the id of the Doctor
     * @param dayOfWeek the day of the week to check
     * @return true if an availability entry already exists
     */
    boolean existsByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}
