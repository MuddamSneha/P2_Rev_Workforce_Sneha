package com.rev.app;

import com.rev.app.dto.AuthRequest;
import com.rev.app.dto.AuthResponse;
import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import com.rev.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        User admin = userRepository.findByEmailIgnoreCase("admin@rev.com")
                .orElse(new User());
        admin.setEmail("admin@rev.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(User.Role.ROLE_ADMIN);
        admin.setIsActive(1);
        userRepository.save(admin);
    }

    @Test
    public void testApiUnauthenticatedAccessDenied() throws Exception {
        // Accessing protected API without token should return 401
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testJwtLoginAndProtectedAccess() throws Exception {
        // Use seeded admin user for login
        String email = "admin@rev.com";
        String password = "password";
        
        // 1. Login to get token
        AuthRequest loginRequest = new AuthRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        String token = authResponse.getToken();

        // 2. Access protected API with token
        mockMvc.perform(get("/api/employees")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void testWebLoginUnaffected() throws Exception {
        // GET /login redirects to / by design (DashboardController.login() returns "redirect:/")
        // Verify the web route is handled correctly and is NOT blocked by the API security chain
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
