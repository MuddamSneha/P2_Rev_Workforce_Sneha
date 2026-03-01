package com.rev.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Lightweight registration request body for POST /api/auth/register.
 * Only requires the fields that can be meaningfully provided via the API.
 * Department, designation, and manager can be assigned later via admin endpoints.
 */
public class ApiRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * One of: ROLE_EMPLOYEE, ROLE_MANAGER, ROLE_ADMIN.
     * Defaults to ROLE_EMPLOYEE if not provided.
     */
    private String role = "ROLE_EMPLOYEE";

    public ApiRegistrationRequest() {}

    public ApiRegistrationRequest(String email, String password, String firstName,
                                   String lastName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /** Converts to the existing RegistrationDto for reuse of UserService.registerUser logic. */
    public RegistrationDto toRegistrationDto() {
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail(this.email);
        dto.setPassword(this.password);
        dto.setFirstName(this.firstName);
        dto.setLastName(this.lastName);
        dto.setRole(this.role);
        return dto;
    }
}
