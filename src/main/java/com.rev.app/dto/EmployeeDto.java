package com.rev.app.dto;

import java.time.LocalDate;
import java.math.BigDecimal;

public class EmployeeDto {
    private String empId;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String emergencyContact;
    private LocalDate dob;
    private LocalDate joiningDate;
    private Long departmentId;
    private String departmentName;
    private Long designationId;
    private String designation;
    private String managerId;
    private String managerName;
    private BigDecimal salary;
    private String ssId;
    private String role;
    private Integer isActive;

    public EmployeeDto() {}

    public EmployeeDto(String empId, Long userId, String email, String firstName, String lastName, String phone, String address, String emergencyContact, LocalDate dob, LocalDate joiningDate, Long departmentId, String departmentName, Long designationId, String designation, String managerId, String managerName, BigDecimal salary, String ssId, String role, Integer isActive) {
        this.empId = empId;
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.dob = dob;
        this.joiningDate = joiningDate;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.designationId = designationId;
        this.designation = designation;
        this.managerId = managerId;
        this.managerName = managerName;
        this.salary = salary;
        this.ssId = ssId;
        this.role = role;
        this.isActive = isActive;
    }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getDesignationId() { return designationId; }
    public void setDesignationId(Long designationId) { this.designationId = designationId; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getSsId() { return ssId; }
    public void setSsId(String ssId) { this.ssId = ssId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
}
