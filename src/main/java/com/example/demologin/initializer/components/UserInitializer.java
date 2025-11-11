package com.example.demologin.initializer.components;

import com.example.demologin.entity.Role;
import com.example.demologin.entity.User;
import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

 @Transactional
    public void initializeUsers() {

     Role studentRole = roleRepository.findByName("STUDENT")
             .orElseGet(() -> {
                 Role r = new Role();
                 r.setName("STUDENT");
                 r.setDescription("Role for students");
                 return roleRepository.save(r);
             });

     Role teacherRole = roleRepository.findByName("TEACHER")
             .orElseGet(() -> {
                 Role r = new Role();
                 r.setName("TEACHER");
                 r.setDescription("Role for teachers");
                 return roleRepository.save(r);
             });
     Role adminRole = roleRepository.findByName("ADMIN")
             .orElseGet(() -> {
                 Role r = new Role();
                 r.setName("ADMIN");
                 r.setDescription("Role for admins");
                 return roleRepository.save(r);
             });

     User admin = new User();
     admin.setUsername("admin");
     admin.setPassword(passwordEncoder.encode("admin123"));

     // === 2️⃣ Tạo user STUDENT ===
     for (int i = 1; i <= 5; i++) {
         String username = "student" + i;
         if (!userRepository.existsByUsername(username)) {
             User user = new User();
             user.setUsername(username);
             user.setPassword(passwordEncoder.encode("123456"));
             user.setFullName("Student " + i);
             user.setEmail(username + "@example.com");
             user.setStatus(UserStatus.ACTIVE);
             user.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
             user.setVerify(true);
             user.setLocked(false);
             user.setRoles(new HashSet<>(Set.of(studentRole)));

             userRepository.save(user);
             log.info("✅ Created user: {}", username);
         }
     }

     // === 3️⃣ Tạo user TEACHER ===
     for (int i = 1; i <= 5; i++) {
         String username = "teacher" + i;
         if (!userRepository.existsByUsername(username)) {
             User user = new User();
             user.setUsername(username);
             user.setPassword(passwordEncoder.encode("123456"));
             user.setFullName("Teacher " + i);
             user.setEmail(username + "@example.com");
             user.setStatus(UserStatus.ACTIVE);
             user.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);
             user.setVerify(true);
             user.setLocked(false);
             user.setRoles(new HashSet<>(Set.of(teacherRole)));

             userRepository.save(user);
             log.info("✅ Created user: {}", username);
         }
     }
 }

}
