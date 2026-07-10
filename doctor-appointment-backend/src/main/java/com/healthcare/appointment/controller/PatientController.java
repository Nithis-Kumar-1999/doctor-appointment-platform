package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.request.PatientRequest;
import com.healthcare.appointment.dto.response.PatientResponse;
import com.healthcare.appointment.service.PatientService;
import com.healthcare.appointment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "3. Patient Profile", description = "Endpoints for managing patient profiles")
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;

    @Operation(summary = "Create patient profile", description = "Creates a profile for a registered user with the PATIENT role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or user is not a PATIENT"),
            @ApiResponse(responseCode = "409", description = "Profile already exists")
    })
    @PostMapping("/profile")
    public ResponseEntity<PatientResponse> createProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PatientRequest request) {
        
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return new ResponseEntity<>(patientService.createProfile(userId, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update patient profile", description = "Updates the authenticated patient's profile details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PutMapping("/profile")
    public ResponseEntity<PatientResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PatientRequest request) {
        
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(patientService.updateProfile(userId, request));
    }

    @Operation(summary = "Get my profile", description = "Retrieves the profile of the currently authenticated patient.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/profile/me")
    public ResponseEntity<PatientResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(patientService.getProfileByUserId(userId));
    }
}
