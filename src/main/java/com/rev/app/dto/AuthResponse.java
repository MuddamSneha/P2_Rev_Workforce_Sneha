package com.rev.app.dto;

/**
 * Response body for POST /api/auth/login.
 * Contains the signed JWT token along with basic user metadata.
 */
public class AuthResponse {

    private String token;
    private String email;
    private String role;
    private long expiresIn; // in seconds

    public AuthResponse() {}

    public AuthResponse(String token, String email, String role, long expiresIn) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
