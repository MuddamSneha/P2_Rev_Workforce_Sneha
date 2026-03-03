package com.rev.app.controller;

import com.rev.app.dto.EmployeeDto;
import com.rev.app.service.ConfigService;
import com.rev.app.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ConfigService configService;

    @GetMapping("/employees")
    public String listEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "empId") String sortBy,
            Model model) {
        
        org.springframework.data.domain.Page<EmployeeDto> employeePage;
        if (search != null && !search.isEmpty()) {
            employeePage = employeeService.searchEmployees(search, page, size, sortBy);
            model.addAttribute("search", search);
        } else {
            employeePage = employeeService.getAllEmployees(page, size, sortBy);
        }
        
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("page", employeePage);
        return "admin/employee-list";
    }

    @GetMapping("/employees/add")
    public String addEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeDto());
        model.addAttribute("departments", configService.getAllDepartments());
        model.addAttribute("designations", configService.getAllDesignations());
        model.addAttribute("managers", employeeService.getAllEmployees(0, Integer.MAX_VALUE, "firstName").getContent());
        return "admin/employee-form";
    }

    @GetMapping("/employees/edit/{id}")
    public String editEmployeeForm(@PathVariable String id, Model model) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("departments", configService.getAllDepartments());
        model.addAttribute("designations", configService.getAllDesignations());
        model.addAttribute("managers", employeeService.getAllEmployees(0, Integer.MAX_VALUE, "firstName").getContent());
        return "admin/employee-form";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute EmployeeDto dto, Model model) {
        try {
            if (dto.getEmpId() != null && !dto.getEmpId().isEmpty() && employeeService.isEmployeeExists(dto.getEmpId())) {
                employeeService.updateEmployee(dto);
            } else {
                employeeService.createEmployee(dto);
            }
            return "redirect:/admin/employees?success=saved";
        } catch (com.rev.app.exceptions.BusinessException ex) {
            model.addAttribute("employee", dto);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("departments", configService.getAllDepartments());
            model.addAttribute("designations", configService.getAllDesignations());
            model.addAttribute("managers", employeeService.getAllEmployees(0, Integer.MAX_VALUE, "firstName").getContent());
            return "admin/employee-form";
        }
    }

    @GetMapping("/employees/deactivate/{id}")
    public String deactivateEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/reactivate/{id}")
    public String reactivateEmployee(@PathVariable String id) {
        employeeService.reactivateEmployee(id);
        return "redirect:/admin/employees";
    }
}
