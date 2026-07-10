package com.healthcare.appointment.service.impl;

import com.healthcare.appointment.dto.request.AppointmentRequest;
import com.healthcare.appointment.dto.response.AppointmentResponse;
import com.healthcare.appointment.entity.Appointment;
import com.healthcare.appointment.entity.Doctor;
import com.healthcare.appointment.entity.DoctorAvailability;
import com.healthcare.appointment.entity.Patient;
import com.healthcare.appointment.enums.AppointmentStatus;
import com.healthcare.appointment.exception.AppointmentConflictException;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.repository.AppointmentRepository;
import com.healthcare.appointment.service.AppointmentService;
import com.healthcare.appointment.service.DoctorAvailabilityService;
import com.healthcare.appointment.service.DoctorService;
import com.healthcare.appointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DoctorAvailabilityService availabilityService;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(Long patientUserId, AppointmentRequest request) {
        // Resolve entities
        Patient patient = patientService.getPatientEntityByUserId(patientUserId);
        Doctor doctor = doctorService.getDoctorEntityById(request.doctorId());

        // Business Rule 1: Validate Doctor Availability on requested day
        DoctorAvailability availability = availabilityService.getAvailabilityForDay(doctor.getId(), request.appointmentDate().getDayOfWeek())
                .orElseThrow(() -> new InvalidOperationException("Doctor is not available on this day of the week"));

        if (!availability.isActive()) {
            throw new InvalidOperationException("Doctor's schedule for this day is currently inactive");
        }

        // Business Rule 2: Validate requested time falls within Doctor's hours
        if (request.appointmentTime().isBefore(availability.getStartTime()) ||
            request.appointmentTime().plusMinutes(availability.getSlotDurationMinutes()).isAfter(availability.getEndTime())) {
            throw new InvalidOperationException("Requested time falls outside the doctor's available hours or slot duration");
        }

        // Business Rule 3: Conflict Check (Double-booking)
        if (appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                doctor.getId(), request.appointmentDate(), request.appointmentTime())) {
            throw new AppointmentConflictException("This time slot is already booked");
        }

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(request.appointmentDate())
                .appointmentTime(request.appointmentTime())
                .reason(request.reason())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToResponse(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getPatientAppointments(Long patientUserId, AppointmentStatus status, Pageable pageable) {
        Patient patient = patientService.getPatientEntityByUserId(patientUserId);
        Page<Appointment> appointments;

        if (status != null) {
            appointments = appointmentRepository.findByPatientIdAndStatus(patient.getId(), status, pageable);
        } else {
            appointments = appointmentRepository.findByPatientId(patient.getId(), pageable);
        }

        return appointments.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getDoctorAppointments(Long doctorUserId, AppointmentStatus status, Pageable pageable) {
        Doctor doctor = doctorService.getDoctorEntityById(doctorService.getProfileByUserId(doctorUserId).id());
        Page<Appointment> appointments;

        if (status != null) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(doctor.getId(), status, pageable);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctor.getId(), pageable);
        }

        return appointments.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus, String reasonOrNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Business Logic: Status transitions
        AppointmentStatus currentStatus = appointment.getStatus();

        if (currentStatus == AppointmentStatus.CANCELLED || currentStatus == AppointmentStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot change status of a " + currentStatus + " appointment");
        }

        if (newStatus == AppointmentStatus.COMPLETED && currentStatus != AppointmentStatus.CONFIRMED) {
            throw new InvalidOperationException("Only CONFIRMED appointments can be marked as COMPLETED");
        }

        appointment.setStatus(newStatus);

        if (newStatus == AppointmentStatus.CANCELLED) {
            appointment.setCancellationReason(reasonOrNotes);
        } else if (newStatus == AppointmentStatus.COMPLETED) {
            appointment.setNotes(reasonOrNotes);
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponse(updatedAppointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        String doctorName = appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName();
        String patientName = appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName();

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getId(),
                doctorName,
                appointment.getDoctor().getSpecialty().getDisplayName(),
                appointment.getPatient().getId(),
                patientName,
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus().name(),
                appointment.getStatus().getDisplayName(),
                appointment.getReason(),
                appointment.getNotes(),
                appointment.getCancellationReason(),
                appointment.getCreatedAt()
        );
    }
}
