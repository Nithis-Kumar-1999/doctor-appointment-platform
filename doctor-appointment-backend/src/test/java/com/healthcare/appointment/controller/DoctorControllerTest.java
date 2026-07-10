package com.healthcare.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.appointment.dto.request.DoctorRequest;
import com.healthcare.appointment.dto.response.DoctorResponse;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.Specialty;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.security.CustomUserDetailsService;
import com.healthcare.appointment.security.jwt.JwtService;
import com.healthcare.appointment.service.DoctorService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;

    @MockBean private DoctorService doctorService;
    @MockBean private UserService userService;
    @MockBean private CustomUserDetailsService userDetailsService;

    private String validToken;
    private final String docEmail = "doc@test.com";

    @BeforeEach
    void setUp() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(docEmail).password("pass").authorities("ROLE_DOCTOR").build();
        when(userDetailsService.loadUserByUsername(docEmail)).thenReturn(userDetails);
        
        validToken = "Bearer " + jwtService.generateToken(userDetails);

        User mockUser = new User();
        mockUser.setId(5L);
        mockUser.setEmail(docEmail);
        when(userService.getUserByEmail(docEmail)).thenReturn(mockUser);
    }

    @Test
    public void testCreateProfile_Success() throws Exception {
        DoctorRequest request = new DoctorRequest(
                Specialty.CARDIOLOGY, "MBBS, MD", 10, new BigDecimal("1000.00"), 
                "+1234567890", "New York", "Bio", "http://image.url"
        );

        DoctorResponse response = new DoctorResponse(
                1L, 5L, "Jane", "Doe", docEmail, "CARDIOLOGY", "Cardiology",
                "MBBS, MD", 10, new BigDecimal("1000.00"), "+1234567890", "New York", 
                "Bio", "http://image.url", true, LocalDateTime.now()
        );

        when(doctorService.createProfile(eq(5L), any(DoctorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/doctors/profile")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialtyDisplayName").value("Cardiology"));
    }

    @Test
    public void testCreateProfile_ValidationFailure() throws Exception {
        // Negative experience years, missing specialty, invalid phone
        DoctorRequest request = new DoctorRequest(
                null, "", -5, new BigDecimal("0"), 
                "invalid", "New York", "Bio", "http://image.url"
        );

        mockMvc.perform(post("/api/v1/doctors/profile")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.experienceYears").exists())
                .andExpect(jsonPath("$.details.specialty").exists())
                .andExpect(jsonPath("$.details.consultationFee").exists())
                .andExpect(jsonPath("$.details.phone").exists());
    }

    @Test
    public void testGetProfileMe_NotFound() throws Exception {
        when(doctorService.getProfileByUserId(5L)).thenThrow(new ResourceNotFoundException("Doctor profile not found"));

        mockMvc.perform(get("/api/v1/doctors/profile/me")
                .header("Authorization", validToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor profile not found"));
    }
}
