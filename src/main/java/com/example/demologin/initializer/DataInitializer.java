package com.example.demologin.initializer;

import com.example.demologin.entity.User;
import com.example.demologin.enums.Gender;
import com.example.demologin.enums.Role;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer is running...");

        if (userRepository.count() == 0) {
            createDefaultUsers();
        }
    }

    private void createDefaultUsers() {
        createUser("admin", "admin123", Role.ADMIN);
        createUser("employee", "emp123", Role.EMPLOYEE);
        createUser("member", "mem123", Role.MEMBER);
    }

    private void createUser(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .fullName(username + " Fullname")
                .email(username + "@example.com")
                .phone("0123456789")
                .address("123 Main Street")
                .identityCard("123456789")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .status(UserStatus.ACTIVE)
                .gender(Gender.OTHER)
                .tokenVersion(0)
                .locked(false)
                .build();

        userRepository.save(user);
        System.out.println("Created user with role: " + role);
    }
}
