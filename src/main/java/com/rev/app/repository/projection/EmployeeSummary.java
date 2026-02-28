package com.rev.app.repository.projection;

/**
 * Projection interface for fetching essential employee details.
 */
public interface EmployeeSummary {
    String getEmpId();
    String getFirstName();
    String getLastName();
    
    // Nested projection or just simple fields
    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
