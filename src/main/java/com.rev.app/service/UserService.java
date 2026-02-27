package com.rev.app.service;

import com.rev.app.dto.RegistrationDto;
import com.rev.app.entity.User;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String password, User.Role role);

    User registerUser(RegistrationDto registrationDto);

    Optional<User> findByEmail(String email);

    void updatePassword(Long userId, String newPassword);

    void setActiveStatus(Long userId, boolean isActive);
}
