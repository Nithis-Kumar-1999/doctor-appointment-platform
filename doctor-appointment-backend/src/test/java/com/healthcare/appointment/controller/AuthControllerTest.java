package com.healthcare.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.appointment.dto.request.RegisterRequest;
import com.healthcare.appointment.dto.response.LoginResponse;
import com.healthcare.appointment.entity.User;
import com.healthcare.appointment.enums.Role;
import com.healthcare.appointment.exception.DuplicateResourceException;
import com.healthcare.appointment.security.CustomUserDetailsService;
import com.healthcare.appointment.security.jwt.JwtService;
import com.healthcare.appointment.service.AuthService;
import com.healthcare.appointment.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Test
    public void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@test.com", "password123", Role.PATIENT);
        LoginResponse response = new LoginResponse("access_token", "refresh_token", "Bearer", 1L, "john@test.com", "John", "PATIENT");
        
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    public void testRegister_ValidationFailure() throws Exception {
        // Invalid email, short password, missing role
        RegisterRequest request = new RegisterRequest("", "", "invalid-email", "short", null);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed for the request."))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists())
                .andExpect(jsonPath("$.details.role").exists());
    }

    @Test
    public void testRegister_DuplicateEmailConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@test.com", "password123", Role.PATIENT);
        when(authService.register(any())).thenThrow(new DuplicateResourceException("Email is already registered"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    public void testLogout_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized()) // Intercepted by JwtAuthenticationEntryPoint
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    public void testLogout_SuccessWithToken() throws Exception {
        String email = "test@test.com";
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(email).password("pass").authorities("ROLE_PATIENT").build();
        
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        String token = jwtService.generateToken(userDetails);

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail(email);
        when(userService.getUserByEmail(email)).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
