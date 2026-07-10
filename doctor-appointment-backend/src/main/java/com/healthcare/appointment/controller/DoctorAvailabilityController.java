package com.healthcare.appointment.controller;

import com.healthcare.appointment.entity.DoctorAvailability;
import com.healthcare.appointment.service.DoctorAvailabilityService;
import com.healthcare.appointment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@Tag(name = "4. Doctor Availability", description = "Endpoints for managing doctor weekly schedules")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;
    private final UserService userService;

    @Operation(summary = "Add availability", description = "Allows a doctor to add available working hours for a specific day of the week.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Availability added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid time range or slot duration")
    })
    @PostMapping
    public ResponseEntity<Void> addAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Day of the week (e.g., MONDAY)") @RequestParam DayOfWeek dayOfWeek,
            @Parameter(description = "Start time in HH:mm:ss format") @RequestParam LocalTime startTime,
            @Parameter(description = "End time in HH:mm:ss format") @RequestParam LocalTime endTime,
            @Parameter(description = "Duration of each appointment slot in minutes") @RequestParam Integer slotDurationMinutes) {
            
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        availabilityService.addAvailability(userId, dayOfWeek, startTime, endTime, slotDurationMinutes);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get doctor schedule", description = "Retrieves the weekly availability schedule for a specific doctor by their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule retrieved successfully")
    })
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorAvailability>> getDoctorSchedule(
            @Parameter(description = "ID of the doctor profile") @PathVariable("doctorId") Long doctorId) {
        
        return ResponseEntity.ok(availabilityService.getDoctorSchedule(doctorId));
    }
}
