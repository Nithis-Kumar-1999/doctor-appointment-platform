package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.request.DoctorRequest;
import com.healthcare.appointment.dto.response.DoctorResponse;
import com.healthcare.appointment.enums.Specialty;
import com.healthcare.appointment.service.DoctorService;
import com.healthcare.appointment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "2. Doctor Profile", description = "Endpoints for managing doctor profiles and searching for doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;

    @Operation(summary = "Create doctor profile", description = "Creates a profile for a registered user with the DOCTOR role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or user is not a DOCTOR"),
            @ApiResponse(responseCode = "409", description = "Profile already exists")
    })
    @PostMapping("/profile")
    public ResponseEntity<DoctorResponse> createProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorRequest request) {
        
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return new ResponseEntity<>(doctorService.createProfile(userId, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update doctor profile", description = "Updates the authenticated doctor's profile details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PutMapping("/profile")
    public ResponseEntity<DoctorResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorRequest request) {
        
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(doctorService.updateProfile(userId, request));
    }

    @Operation(summary = "Get my profile", description = "Retrieves the profile of the currently authenticated doctor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/profile/me")
    public ResponseEntity<DoctorResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(doctorService.getProfileByUserId(userId));
    }

    @Operation(summary = "Search doctors", description = "Paginated search for doctors by specialty or city.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results retrieved")
    })
    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> searchDoctors(
            @RequestParam(required = false) Specialty specialty,
            @RequestParam(required = false) String city,
            Pageable pageable) {
        
        return ResponseEntity.ok(doctorService.searchDoctors(specialty, city, pageable));
    }
}
