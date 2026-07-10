package com.healthcare.appointment.controller;

import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.security.CustomUserDetailsService;
import com.healthcare.appointment.security.jwt.JwtService;
import com.healthcare.appointment.service.DoctorAvailabilityService;
import com.healthcare.appointment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorAvailabilityControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private DoctorAvailabilityService availabilityService;
    @MockBean private UserService userService;
    @MockBean private CustomUserDetailsService userDetailsService;

    private String validToken;

    @BeforeEach
    void setUp() {
        String docEmail = "schedule@test.com";
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(docEmail).password("pass").authorities("ROLE_DOCTOR").build();
        when(userDetailsService.loadUserByUsername(docEmail)).thenReturn(userDetails);
        
        validToken = "Bearer " + jwtService.generateToken(userDetails);

        User mockUser = new User();
        mockUser.setId(11L);
        when(userService.getUserByEmail(docEmail)).thenReturn(mockUser);
    }

    @Test
    public void testAddAvailability_Success() throws Exception {
        mockMvc.perform(post("/api/v1/availability")
                .header("Authorization", validToken)
                .param("dayOfWeek", "MONDAY")
                .param("startTime", "09:00:00")
                .param("endTime", "17:00:00")
                .param("slotDurationMinutes", "30"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddAvailability_MissingParam_BadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/availability")
                .header("Authorization", validToken)
                .param("dayOfWeek", "MONDAY")
                // Missing startTime, endTime, slotDurationMinutes
                )
                .andExpect(status().isBadRequest());
    }
}
