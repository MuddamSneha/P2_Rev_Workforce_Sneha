package com.rev.app.service;

import com.rev.app.dto.RegistrationDto;
import com.rev.app.entity.Employee;
import com.rev.app.entity.User;
import com.rev.app.exceptions.BusinessException;
import com.rev.app.repository.DepartmentRepository;
import com.rev.app.repository.DesignationRepository;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.UserRepository;
import com.rev.app.service.LeaveService;
import com.rev.app.service.UserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private AutoCloseable closeable;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private LeaveService leaveService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private RegistrationDto registrationDto;

    @Before
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        closeable = org.mockito.MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@email.com");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        user.setIsActive(1);

        registrationDto = new RegistrationDto();
        registrationDto.setEmail("test@email.com");
        registrationDto.setPassword("password");
        registrationDto.setRole("ROLE_EMPLOYEE");
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void whenCreateUser_thenReturnUser() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.createUser("test@email.com", "password", User.Role.ROLE_EMPLOYEE);

        assertThat(created.getEmail()).isEqualTo("test@email.com");
        verify(userRepository).save(any(User.class));
    }

    @Test(expected = BusinessException.class)
    public void whenCreateExistingUser_thenThrowException() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));

        userService.createUser("test@email.com", "password", User.Role.ROLE_EMPLOYEE);
    }

    @Test
    public void whenRegisterUser_thenReturnUserAndSaveEmployee() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(leaveService).initializeLeaveBalances(anyString());

        User result = userService.registerUser(registrationDto);

        assertThat(result.getEmail()).isEqualTo("test@email.com");
        verify(employeeRepository).save(any(Employee.class));
        verify(leaveService).initializeLeaveBalances(anyString());
    }

    @Test(expected = BusinessException.class)
    public void whenRegisterExistingUser_thenThrowException() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));

        userService.registerUser(registrationDto);
    }

    @Test
    public void whenFindByEmail_thenReturnOptionalUser() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@email.com");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    public void whenUpdatePassword_thenSaveUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        userService.updatePassword(1L, "newPassword");

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void whenSetActiveStatus_thenUpdateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.setActiveStatus(1L, false);

        assertThat(user.getIsActive()).isEqualTo(0);
        verify(userRepository).save(any(User.class));
    }
}
