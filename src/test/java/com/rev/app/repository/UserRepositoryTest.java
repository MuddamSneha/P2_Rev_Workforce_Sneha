package com.rev.app.repository;

import com.rev.app.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByEmailIgnoreCase_thenReturnUser() {
        User user = new User();
        user.setEmail("TEST@example.com");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailIgnoreCase("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("TEST@example.com");
    }

    @Test
    public void whenExistsByEmailIgnoreCase_thenReturnTrue() {
        User user = new User();
        user.setEmail("EXISTS@example.com");
        user.setPassword("password");
        user.setRole(User.Role.ROLE_EMPLOYEE);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmailIgnoreCase("exists@example.com");

        assertThat(exists).isTrue();
    }
}
