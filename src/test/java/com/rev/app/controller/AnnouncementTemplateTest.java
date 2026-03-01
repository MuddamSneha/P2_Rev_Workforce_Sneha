package com.rev.app.controller;

import com.rev.app.dto.AnnouncementDto;
import com.rev.app.service.AnnouncementService;
import com.rev.app.service.ConfigService;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Tests Announcement-related Thymeleaf template rendering via AdminConfigController.
 *
 * Security auto-configuration is excluded because @WebMvcTest's component scan
 * would otherwise try to instantiate JwtAuthenticationFilter (a @Component), which
 * field-injects JwtUtil — creating a bean dependency chain that cannot be satisfied
 * by @MockBean alone in a sliced context.
 *
 * Instead, we exclude SecurityAutoConfiguration and SecurityFilterAutoConfiguration
 * so no security filter chain is built at all for this test.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(
    value = AdminConfigController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            com.rev.app.config.SecurityConfig.class,
            com.rev.app.security.JwtAuthenticationFilter.class
        }
    )
)
public class AnnouncementTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigService configService;

    @MockBean
    private AnnouncementService announcementService;

    @MockBean
    private EmployeeService employeeService;

    // Required by GlobalControllerAdvice (@ControllerAdvice picked up by @WebMvcTest)
    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

    @Test
    public void testAnnouncementsTemplate() throws Exception {
        AnnouncementDto mockAnn = new AnnouncementDto();
        mockAnn.setAnnouncementId(1L);
        mockAnn.setTitle("Test Title");
        mockAnn.setMessage("Test message");
        mockAnn.setCreatedBy("admin@revworkforce.com");
        mockAnn.setCreatorName("Admin User");
        mockAnn.setCreatedAt(LocalDateTime.now());

        Page<AnnouncementDto> mockPage = new PageImpl<>(
                Collections.singletonList(mockAnn), PageRequest.of(0, 10), 1);
        Mockito.when(announcementService.getAllAnnouncements(0, 10)).thenReturn(mockPage);

        mockMvc.perform(get("/admin/config/announcements"))
                .andDo(print());
    }
}
