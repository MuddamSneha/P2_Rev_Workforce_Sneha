package com.rev.app.rest;

import com.rev.app.config.SecurityConfig;
import com.rev.app.dto.AuthRequest;
import com.rev.app.entity.User;
import com.rev.app.security.JwtAccessDeniedHandler;
import com.rev.app.security.JwtAuthenticationEntryPoint;
import com.rev.app.security.JwtAuthenticationFilter;
import com.rev.app.security.JwtUtil;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
public class AuthControllerTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserDetailsService userDetailsService;



    @Test
    public void whenLoginWithValidCredentials_thenReturnToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", "password");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        // Principal must be UserDetails
        Authentication fullyAuth = new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(fullyAuth);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock-token");

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    public void whenLoginWithInvalidCredentials_thenReturnUnauthorized() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid"));

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"wrong@example.com\",\"password\":\"wrong\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenRegister_thenReturnCreated() throws Exception {
        User user = new User();
        user.setEmail("new@example.com");
        user.setRole(User.Role.ROLE_EMPLOYEE);

        when(userService.registerUser(any())).thenReturn(user);

        String json = "{\"email\":\"new@example.com\",\"password\":\"password\",\"firstName\":\"First\",\"lastName\":\"Last\",\"role\":\"ROLE_EMPLOYEE\"}";

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    public void whenLoginWithInvalidRequest_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"password\":\"\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
