package com.rev.app.controller;

import com.rev.app.dto.AnnouncementDto;
import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import com.rev.app.service.AnnouncementService;
import com.rev.app.service.ConfigService;
import com.rev.app.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin/config")
public class AdminConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private EmployeeService employeeService;

    // Departments
    @GetMapping("/departments")
    public String manageDepartments(Model model) {
        model.addAttribute("departments", configService.getAllDepartments());
        model.addAttribute("newDept", new Department());
        return "admin/config/departments";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@ModelAttribute("newDept") Department department, Principal principal) {
        configService.saveDepartment(department);
        if (principal != null) {
            configService.logActivity("MANAGE_DEPARTMENT", principal.getName(), "Saved department: " + department.getDepartmentName());
        }
        return "redirect:/admin/config/departments";
    }

    @GetMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, Principal principal) {
        configService.deleteDepartment(id);
        if (principal != null) {
            configService.logActivity("DELETE_DEPARTMENT", principal.getName(), "Deleted department ID: " + id);
        }
        return "redirect:/admin/config/departments";
    }

    // Designations
    @GetMapping("/designations")
    public String manageDesignations(Model model) {
        model.addAttribute("designations", configService.getAllDesignations());
        model.addAttribute("newDesignation", new Designation());
        return "admin/config/designations";
    }

    @PostMapping("/designations/save")
    public String saveDesignation(@ModelAttribute("newDesignation") Designation designation, Principal principal) {
        configService.saveDesignation(designation);
        if (principal != null) {
            configService.logActivity("MANAGE_DESIGNATION", principal.getName(), "Saved designation: " + designation.getDesignationName());
        }
        return "redirect:/admin/config/designations";
    }

    @GetMapping("/designations/delete/{id}")
    public String deleteDesignation(@PathVariable Long id, Principal principal) {
        configService.deleteDesignation(id);
        if (principal != null) {
            configService.logActivity("DELETE_DESIGNATION", principal.getName(), "Deleted designation ID: " + id);
        }
        return "redirect:/admin/config/designations";
    }

    // Announcements
    @GetMapping("/announcements")
    public String manageAnnouncements(Model model) {
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        model.addAttribute("newAnnouncement", new AnnouncementDto());
        return "admin/config/announcements";
    }

    @PostMapping("/announcements/save")
    public String saveAnnouncement(@ModelAttribute("newAnnouncement") AnnouncementDto dto, Principal principal) {
        if (dto.getAnnouncementId() != null) {
            announcementService.updateAnnouncement(dto);
            if (principal != null) {
                configService.logActivity("UPDATE_ANNOUNCEMENT", principal.getName(), "Updated announcement: " + dto.getTitle());
            }
        } else {
            if (principal != null) {
                dto.setCreatedBy(principal.getName());
                announcementService.createAnnouncement(dto);
                configService.logActivity("CREATE_ANNOUNCEMENT", principal.getName(), "Created announcement: " + dto.getTitle());
            }
        }
        return "redirect:/admin/config/announcements";
    }

    @GetMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, Principal principal) {
        announcementService.deleteAnnouncement(id);
        if (principal != null) {
            configService.logActivity("DELETE_ANNOUNCEMENT", principal.getName(), "Deleted announcement ID: " + id);
        }
        return "redirect:/admin/config/announcements";
    }

    // Activity Logs
    @GetMapping("/logs")
    public String viewLogs(Model model) {
        model.addAttribute("logs", configService.getAllLogs());
        return "admin/config/logs";
    }
}
