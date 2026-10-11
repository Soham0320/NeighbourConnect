package com.example.website.service;

import com.example.website.dto.RegisterForm;
import com.example.website.entity.AppUser;
import com.example.website.entity.Role;
import com.example.website.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public AppUserService(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser u = users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
        return User.withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .disabled(!u.isEnabled())
                .build();
    }

    @Transactional
    public AppUser register(RegisterForm form) {
        return create(form.getFullName(), form.getEmail(), form.getPassword(), Role.USER);
    }

    @Transactional
    public AppUser create(String fullName, String email, String rawPassword, Role role) {
        String normalized = email.trim().toLowerCase();
        if (users.existsByEmail(normalized)) {
            throw new IllegalArgumentException("Email already registered");
        }
        AppUser u = new AppUser();
        u.setFullName(fullName);
        u.setEmail(normalized);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(role);
        return users.save(u);
    }

    public boolean emailExists(String email) {
        return users.existsByEmail(email.trim().toLowerCase());
    }
}
