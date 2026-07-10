package com.healthcare.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.appointment.dto.request.AppointmentRequest;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.AppointmentStatus;
import com.healthcare.appointment.exception.AppointmentConflictException;
import com.healthcare.appointment.security.CustomUserDetailsService;
import com.healthcare.appointment.security.jwt.JwtService;
import com.healthcare.appointment.service.AppointmentService;
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
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;

    @MockBean private AppointmentService appointmentService;
    @MockBean private UserService userService;
    @MockBean private CustomUserDetailsService userDetailsService;

    private String validToken;
    private final String email = "patient@test.com";

    @BeforeEach
    void setUp() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(email).password("pass").authorities("ROLE_PATIENT").build();
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        
        validToken = "Bearer " + jwtService.generateToken(userDetails);

        User mockUser = new User();
        mockUser.setId(99L);
        when(userService.getUserByEmail(email)).thenReturn(mockUser);
    }

    @Test
    public void testBookAppointment_Conflict() throws Exception {
        AppointmentRequest request = new AppointmentRequest(
                1L, LocalDate.now().plusDays(5), LocalTime.of(10, 30), "Checkup"
        );

        when(appointmentService.bookAppointment(eq(99L), any())).thenThrow(
                new AppointmentConflictException("This time slot is already booked")
        );

        mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This time slot is already booked"));
    }

    @Test
    public void testUpdateStatus_Success() throws Exception {
        mockMvc.perform(patch("/api/v1/appointments/100/status")
                .header("Authorization", validToken)
                .param("status", AppointmentStatus.CANCELLED.name())
                .param("reasonOrNotes", "Doctor sick"))
                .andExpect(status().isOk());
    }
}
