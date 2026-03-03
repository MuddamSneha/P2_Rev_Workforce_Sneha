package com.rev.app;

import com.rev.app.dto.RegistrationDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidRegistrationDto() {
        RegistrationDto dto = new RegistrationDto(
                "test@example.com",
                "password123",
                "John",
                "Doe",
                null,
                null,
                null,
                "ROLE_EMPLOYEE"
        );

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmail() {
        RegistrationDto dto = new RegistrationDto(
                "invalid-email",
                "password123",
                "John",
                "Doe",
                null,
                null,
                null,
                "ROLE_EMPLOYEE"
        );

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Please provide a valid email address", violations.iterator().next().getMessage());
    }

    @Test
    public void testShortPassword() {
        RegistrationDto dto = new RegistrationDto(
                "test@example.com",
                "123",
                "John",
                "Doe",
                null,
                null,
                null,
                "ROLE_EMPLOYEE"
        );

        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Password must be at least 6 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void testMissingFields() {
        RegistrationDto dto = new RegistrationDto();
        Set<ConstraintViolation<RegistrationDto>> violations = validator.validate(dto);
        // email, password, firstName, lastName, role are all @NotBlank
        assertTrue(violations.size() >= 5);
    }
}
