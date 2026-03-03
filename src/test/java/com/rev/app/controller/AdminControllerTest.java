package com.rev.app.controller;

import com.rev.app.config.SecurityConfig;
import com.rev.app.controller.AdminController;
import com.rev.app.dto.EmployeeDto;
import com.rev.app.security.JwtAccessDeniedHandler;
import com.rev.app.security.JwtAuthenticationEntryPoint;
import com.rev.app.security.JwtAuthenticationFilter;
import com.rev.app.security.JwtUtil;
import com.rev.app.service.ConfigService;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
public class AdminControllerTest {

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
    private ConfigService configService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;



    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenListEmployees_thenReturnEmployeeListView() throws Exception {
        Page<EmployeeDto> employeePage = new PageImpl<>(Collections.emptyList());
        when(employeeService.getAllEmployees(anyInt(), anyInt(), anyString())).thenReturn(employeePage);

        mockMvc.perform(get("/admin/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-list"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenAddEmployeeForm_thenReturnEmployeeFormView() throws Exception {
        when(configService.getAllDepartments()).thenReturn(Collections.emptyList());
        when(configService.getAllDesignations()).thenReturn(Collections.emptyList());
        when(employeeService.getAllEmployees(anyInt(), anyInt(), anyString())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/admin/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-form"))
                .andExpect(model().attributeExists("employee"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("designations"))
                .andExpect(model().attributeExists("managers"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenListEmployeesWithInsufficientRole_thenReturnForbidden() throws Exception {
        // Mock default behavior to prevent NPE if security is bypassed or request reaches controller
        when(employeeService.getAllEmployees(anyInt(), anyInt(), anyString())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/admin/employees"))
                .andExpect(status().isForbidden());
    }
}
