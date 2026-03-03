package com.rev.app.rest;

import com.rev.app.config.SecurityConfig;
import com.rev.app.dto.NotificationDto;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NotificationRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
public class NotificationRestControllerTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private UserService userService;

    // Required by GlobalControllerAdvice or Security
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtUtil jwtUtil;


    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void whenGetUserNotifications_thenReturnList() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.setMessage("Test Notification");
        when(notificationService.getUserNotifications(anyLong())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Test Notification"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenGetAllNotifications_thenReturnList() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.setMessage("Global Notification");
        when(notificationService.getAllNotifications()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/notifications/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Global Notification"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "EMPLOYEE")
    public void whenGetCurrentUser_thenReturnUserMap() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setRole(User.Role.ROLE_EMPLOYEE);

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(notificationService.getUserNotifications(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    public void whenUnauthenticated_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/notifications/all"))
                .andExpect(status().isUnauthorized());
    }
}
