package com.rev.app.rest;

import com.rev.app.config.SecurityConfig;
import com.rev.app.dto.EmployeeDto;
import com.rev.app.exceptions.ResourceNotFoundException;
import com.rev.app.rest.EmployeeRestController;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
public class EmployeeRestTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtUtil jwtUtil;
    


    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenGetEmployee_thenReturnEmployeeJson() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmpId("EMP001");
        employeeDto.setFirstName("John");
        employeeDto.setLastName("Doe");
        when(employeeService.getEmployeeById("EMP001")).thenReturn(employeeDto);

        mockMvc.perform(get("/api/employees/EMP001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.empId").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenGetNonExistentEmployee_thenReturnNotFound() throws Exception {
        when(employeeService.getEmployeeById(anyString())).thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenCreateEmployee_thenReturnCreatedEmployeeJson() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmpId("EMP001");
        employeeDto.setFirstName("John");
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        String json = "{\"empId\":\"EMP001\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"test@example.com\",\"role\":\"ROLE_EMPLOYEE\"}";

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empId").value("EMP001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenCreateInvalidEmployee_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenDeleteEmployee_thenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/employees/EMP001"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "INSUFFICIENT_ROLE")
    public void whenAccessRestrictedEndpointWithInsufficientRole_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isForbidden());
    }
}
