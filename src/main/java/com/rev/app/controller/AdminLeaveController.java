package com.rev.app.controller;

import com.rev.app.entity.HolidayCalendar;
import com.rev.app.entity.LeaveType;
import com.rev.app.service.ConfigService;
import com.rev.app.service.HolidayService;
import com.rev.app.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/leaves")
public class AdminLeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ConfigService configService;

    @GetMapping
    public String leaveReport(@RequestParam(required = false) String empId,
                             @RequestParam(required = false) Long deptId,
                             @RequestParam(required = false, defaultValue = "ALL") String status,
                             Model model) {
        model.addAttribute("leaves", leaveService.searchLeaves(empId, deptId, status));
        model.addAttribute("departments", configService.getAllDepartments());
        model.addAttribute("empId", empId);
        model.addAttribute("deptId", deptId);
        model.addAttribute("status", status);
        return "admin/leaves/report";
    }

    @GetMapping("/types")
    public String configureTypes(Model model) {
        model.addAttribute("types", leaveService.getAllLeaveTypes());
        model.addAttribute("newType", new LeaveType());
        return "admin/leaves/types";
    }

    @PostMapping("/types/save")
    public String saveType(@ModelAttribute LeaveType leaveType) {
        leaveService.saveLeaveType(leaveType);
        return "redirect:/admin/leaves/types";
    }

    @PostMapping("/types/delete/{id}")
    public String deleteType(@PathVariable Long id) {
        leaveService.deleteLeaveType(id);
        return "redirect:/admin/leaves/types";
    }

    @GetMapping("/balances")
    public String adjustBalances(Model model) {
        model.addAttribute("balances", leaveService.getAllBalances());
        model.addAttribute("leaveTypes", leaveService.getAllLeaveTypes());
        return "admin/leaves/balances";
    }

    @PostMapping("/balances/adjust")
    public String doAdjust(@RequestParam String empId, 
                          @RequestParam Long leaveTypeId, 
                          @RequestParam Integer adjustment) {
        leaveService.adjustBalance(empId, leaveTypeId, adjustment);
        return "redirect:/admin/leaves/balances";
    }

    @GetMapping("/holidays")
    public String manageHolidays(Model model) {
        model.addAttribute("holidays", holidayService.getAllHolidays());
        model.addAttribute("newHoliday", new HolidayCalendar());
        return "admin/leaves/holidays";
    }

    @PostMapping("/holidays/save")
    public String saveHoliday(@ModelAttribute HolidayCalendar holiday) {
        holidayService.saveHoliday(holiday);
        return "redirect:/admin/leaves/holidays";
    }

    @PostMapping("/holidays/delete/{id}")
    public String deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return "redirect:/admin/leaves/holidays";
    }
}
