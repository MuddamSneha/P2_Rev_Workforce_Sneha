package com.rev.app.dto;

import java.time.LocalDate;

public class EmployeeReportDto {
    private String empId;
    private String firstName;
    private String lastName;
    private String email;
    private String departmentName;
    private String designationName;
    private String managerName;
    private LocalDate joiningDate;
    private String status;

    public EmployeeReportDto() {}

    public EmployeeReportDto(String empId, String firstName, String lastName, String email, String departmentName, String designationName, String managerName, LocalDate joiningDate, String status) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.departmentName = departmentName;
        this.designationName = designationName;
        this.managerName = managerName;
        this.joiningDate = joiningDate;
        this.status = status;
    }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getDesignationName() { return designationName; }
    public void setDesignationName(String designationName) { this.designationName = designationName; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
