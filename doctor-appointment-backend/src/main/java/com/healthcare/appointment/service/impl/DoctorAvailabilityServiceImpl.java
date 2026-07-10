package com.healthcare.appointment.service.impl;

import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.entity.DoctorAvailability;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.repository.DoctorAvailabilityRepository;
import com.healthcare.appointment.service.DoctorAvailabilityService;
import com.healthcare.appointment.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorService doctorService;

    @Override
    @Transactional
    public void addAvailability(Long doctorUserId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Integer slotDurationMinutes) {
        Doctor doctor = doctorService.getDoctorEntityById(doctorService.getProfileByUserId(doctorUserId).id());

        // Business Rule: Start time must be before end time
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new InvalidOperationException("End time must be strictly after start time");
        }

        // Business Rule: Duplicate guard per day
        if (availabilityRepository.existsByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek)) {
            throw new DuplicateResourceException("Availability for " + dayOfWeek + " already exists. Update it instead.");
        }

        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .slotDurationMinutes(slotDurationMinutes)
                .build();

        availabilityRepository.save(availability);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAvailability> getDoctorSchedule(Long doctorId) {
        return availabilityRepository.findByDoctorIdAndActiveTrue(doctorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorAvailability> getAvailabilityForDay(Long doctorId, DayOfWeek dayOfWeek) {
        return availabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
    }
}
