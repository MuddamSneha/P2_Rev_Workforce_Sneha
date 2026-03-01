package com.rev.app;

import com.rev.app.dto.AuthRequest;
import com.rev.app.dto.AuthResponse;
import com.rev.app.dto.ApiRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAdminRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.rev.app.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        com.rev.app.entity.User admin = userRepository.findByEmailIgnoreCase("admin@rev.com")
                .orElse(new com.rev.app.entity.User());
        admin.setEmail("admin@rev.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(com.rev.app.entity.User.Role.ROLE_ADMIN);
        admin.setIsActive(1);
        userRepository.save(admin);

        com.rev.app.entity.User employee = userRepository.findByEmailIgnoreCase("employee@rev.com")
                .orElse(new com.rev.app.entity.User());
        employee.setEmail("employee@rev.com");
        employee.setPassword(passwordEncoder.encode("password"));
        employee.setRole(com.rev.app.entity.User.Role.ROLE_EMPLOYEE);
        employee.setIsActive(1);
        userRepository.save(employee);
    }

    @Test
    public void testAnonymousRegistrationFailsWith401() throws Exception {
        ApiRegistrationRequest regRequest = new ApiRegistrationRequest(
                "anon@test.com", "Password123", "Anon", "User", "ROLE_EMPLOYEE"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("authentication is required")));
    }

    @Test
    public void testNonAdminRegistrationFailsWith403() throws Exception {
        // 1. Login as employee to get token
        AuthRequest loginRequest = new AuthRequest("employee@rev.com", "password");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = authResponse.getToken();

        // 2. Try to register a user
        ApiRegistrationRequest regRequest = new ApiRegistrationRequest(
                "newuser@test.com", "Password123", "New", "User", "ROLE_EMPLOYEE"
        );

        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.message", containsString("do not have the required permissions")));
    }

    @Test
    public void testAdminRegistrationSucceeds() throws Exception {
        // 1. Login as admin to get token
        AuthRequest loginRequest = new AuthRequest("admin@rev.com", "password");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        String token = authResponse.getToken();

        // 2. Register a user
        String email = "admin_added_" + System.currentTimeMillis() + "@test.com";
        ApiRegistrationRequest regRequest = new ApiRegistrationRequest(
                email, "Password123", "Admin", "Added", "ROLE_EMPLOYEE"
        );

        mockMvc.perform(post("/api/auth/register")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User registered successfully")))
                .andExpect(jsonPath("$.email", is(email)));
    }
}
