package com.luzdorefugio.service;

import com.luzdorefugio.domain.enums.Role;
import com.luzdorefugio.domain.User;
import com.luzdorefugio.dto.admin.auth.AuthResponse;
import com.luzdorefugio.dto.admin.auth.LoginRequest;
import com.luzdorefugio.dto.admin.auth.RegisterRequest;
import com.luzdorefugio.dto.admin.auth.UserProfileRequest;
import com.luzdorefugio.repository.UserRepository;
import com.luzdorefugio.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request, Role role) {
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getRole().name(), user.getName(),
                null, null, null, null, null);
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = repository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getRole().name(), user.getName(), user.getPhone(), user.getNif(),
                user.getAddress(),user.getCity(), user.getZipCode());
    }

    public AuthResponse updateUserProfile(String email, UserProfileRequest request) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.nif() != null) user.setNif(request.nif());
        if (request.address() != null) user.setAddress(request.address());
        if (request.city() != null) user.setCity(request.city());
        if (request.zipCode() != null) user.setZipCode(request.zipCode());
        user = repository.save(user);
        return new AuthResponse(null, user.getRole().name(), user.getName(), user.getPhone(), user.getNif(),
                user.getAddress(),user.getCity(), user.getZipCode());
    }
}