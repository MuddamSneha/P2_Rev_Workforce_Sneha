package com.rev.app.rest;

import com.rev.app.config.SecurityConfig;
import com.rev.app.dto.LeaveDto;
import com.rev.app.rest.LeaveRestController;
import com.rev.app.security.JwtAccessDeniedHandler;
import com.rev.app.security.JwtAuthenticationEntryPoint;
import com.rev.app.security.JwtAuthenticationFilter;
import com.rev.app.security.JwtUtil;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.LeaveService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LeaveRestController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
public class LeaveRestControllerTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveService leaveService;

    // Required by GlobalControllerAdvice or Security
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private EmployeeService employeeService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtUtil jwtUtil;


    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void whenGetEmployeeLeaves_thenReturnPage() throws Exception {
        Page<LeaveDto> page = new PageImpl<>(Collections.emptyList());
        when(leaveService.getEmployeeLeaves(anyString(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/leaves/employee/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void whenApplyLeave_thenReturnCreated() throws Exception {
        LeaveDto dto = new LeaveDto();
        dto.setEmpId("EMP001");
        when(leaveService.applyLeave(any(LeaveDto.class))).thenReturn(dto);

        String json = "{\"empId\":\"EMP001\",\"leaveTypeId\":1,\"startDate\":\"2023-10-10\",\"endDate\":\"2023-10-12\",\"reason\":\"Vacation\"}";

        mockMvc.perform(post("/api/leaves/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empId").value("EMP001"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void whenApproveLeave_thenReturnUpdated() throws Exception {
        LeaveDto dto = new LeaveDto();
        dto.setLeaveId(1L);
        dto.setStatus("APPROVED");
        when(leaveService.approveLeave(anyLong(), anyString(), anyString())).thenReturn(dto);

        mockMvc.perform(put("/api/leaves/approve/1")
                .param("managerId", "MGR001")
                .param("comment", "Approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void whenApplyInvalidLeave_thenReturnBadRequest() throws Exception {
        // Assuming some validation exists or we can mock it
        mockMvc.perform(post("/api/leaves/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }
}
