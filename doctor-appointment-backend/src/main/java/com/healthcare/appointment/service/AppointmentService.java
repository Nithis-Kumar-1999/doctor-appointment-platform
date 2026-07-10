package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.request.AppointmentRequest;
import com.healthcare.appointment.dto.response.AppointmentResponse;
import com.healthcare.appointment.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

    AppointmentResponse bookAppointment(Long patientUserId, AppointmentRequest request);

    Page<AppointmentResponse> getPatientAppointments(Long patientUserId, AppointmentStatus status, Pageable pageable);

    Page<AppointmentResponse> getDoctorAppointments(Long doctorUserId, AppointmentStatus status, Pageable pageable);

    AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus, String reasonOrNotes);
}
