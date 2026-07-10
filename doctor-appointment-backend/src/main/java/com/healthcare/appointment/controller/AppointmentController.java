package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.request.AppointmentRequest;
import com.healthcare.appointment.dto.response.AppointmentResponse;
import com.healthcare.appointment.enums.AppointmentStatus;
import com.healthcare.appointment.service.AppointmentService;
import com.healthcare.appointment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "5. Appointments", description = "Endpoints for booking, listing, and managing appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    @Operation(summary = "Book an appointment", description = "Allows an authenticated patient to book a slot with a doctor.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment booked successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Doctor or patient not found"),
            @ApiResponse(responseCode = "409", description = "Time slot is already booked")
    })
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AppointmentRequest request) {
        
        Long patientUserId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return new ResponseEntity<>(appointmentService.bookAppointment(patientUserId, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get my appointments (Patient)", description = "Retrieves paginated list of appointments for the logged-in patient.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    })
    @GetMapping("/patient/me")
    public ResponseEntity<Page<AppointmentResponse>> getPatientAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Filter by status (e.g., SCHEDULED, COMPLETED)") @RequestParam(required = false) AppointmentStatus status,
            Pageable pageable) {
        
        Long patientUserId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientUserId, status, pageable));
    }

    @Operation(summary = "Get my appointments (Doctor)", description = "Retrieves paginated list of appointments for the logged-in doctor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully")
    })
    @GetMapping("/doctor/me")
    public ResponseEntity<Page<AppointmentResponse>> getDoctorAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Filter by status (e.g., SCHEDULED, COMPLETED)") @RequestParam(required = false) AppointmentStatus status,
            Pageable pageable) {
        
        Long doctorUserId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorUserId, status, pageable));
    }

    @Operation(summary = "Update appointment status", description = "Allows a doctor or admin to update the status of an appointment.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @Parameter(description = "Appointment ID") @PathVariable("id") Long appointmentId,
            @Parameter(description = "New status") @RequestParam AppointmentStatus status,
            @Parameter(description = "Reason or cancellation notes") @RequestParam(required = false) String reasonOrNotes) {
        
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, status, reasonOrNotes));
    }
}
