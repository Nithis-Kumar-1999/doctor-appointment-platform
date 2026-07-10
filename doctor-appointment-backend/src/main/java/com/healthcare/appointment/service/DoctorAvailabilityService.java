package com.healthcare.appointment.service;

import com.healthcare.appointment.entity.DoctorAvailability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorAvailabilityService {

    void addAvailability(Long doctorUserId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Integer slotDurationMinutes);

    List<DoctorAvailability> getDoctorSchedule(Long doctorId);

    Optional<DoctorAvailability> getAvailabilityForDay(Long doctorId, DayOfWeek dayOfWeek);
}
