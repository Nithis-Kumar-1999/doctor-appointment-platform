package com.healthcare.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.appointment.dto.request.PatientRequest;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.Gender;
import com.healthcare.appointment.exception.InvalidOperationException;
import com.healthcare.appointment.security.CustomUserDetailsService;
import com.healthcare.appointment.security.jwt.JwtService;
import com.healthcare.appointment.service.PatientService;
import com.healthcare.appointment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;

    @MockBean private PatientService patientService;
    @MockBean private UserService userService;
    @MockBean private CustomUserDetailsService userDetailsService;

    private String validToken;
    private final String patientEmail = "patient@test.com";

    @BeforeEach
    void setUp() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(patientEmail).password("pass").authorities("ROLE_PATIENT").build();
        when(userDetailsService.loadUserByUsername(patientEmail)).thenReturn(userDetails);
        
        validToken = "Bearer " + jwtService.generateToken(userDetails);

        User mockUser = new User();
        mockUser.setId(8L);
        mockUser.setEmail(patientEmail);
        when(userService.getUserByEmail(patientEmail)).thenReturn(mockUser);
    }

    @Test
    public void testCreateProfile_ValidationFailure_FutureDate() throws Exception {
        // Validation failure: @Past rule violated
        PatientRequest request = new PatientRequest(
                LocalDate.now().plusDays(10), // Future date
                Gender.MALE,
                "+1234567890",
                "123 Main St",
                "O+",
                "+0987654321"
        );

        mockMvc.perform(post("/api/v1/patients/profile")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.dateOfBirth").exists());
    }

    @Test
    public void testCreateProfile_BadRequest_InvalidRole() throws Exception {
        PatientRequest request = new PatientRequest(
                LocalDate.of(1990, 1, 1),
                Gender.MALE,
                "+1234567890",
                "123 Main St",
                "O+",
                "+0987654321"
        );

        // Mock service throwing business exception
        when(patientService.createProfile(eq(8L), any())).thenThrow(
                new InvalidOperationException("User role must be PATIENT to create a patient profile")
        );

        mockMvc.perform(post("/api/v1/patients/profile")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User role must be PATIENT to create a patient profile"));
    }
}
