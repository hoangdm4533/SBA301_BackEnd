package com.example.demologin.utils;

import com.example.demologin.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Không có người dùng nào đang đăng nhập.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new UsernameNotFoundException("Principal không phải là kiểu User.");
        }

        return (User) principal;
    }
}
