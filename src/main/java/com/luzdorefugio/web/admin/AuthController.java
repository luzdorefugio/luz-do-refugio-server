package com.luzdorefugio.web.admin;

import com.luzdorefugio.domain.enums.Role;
import com.luzdorefugio.dto.admin.auth.AuthResponse;
import com.luzdorefugio.dto.admin.auth.LoginRequest;
import com.luzdorefugio.dto.admin.auth.RegisterRequest;
import com.luzdorefugio.dto.admin.auth.UserProfileRequest;
import com.luzdorefugio.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService service;

    @PostMapping("/admin/register")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request, Role.ADMIN));
    }

    @PostMapping("/shop/register")
    public ResponseEntity<AuthResponse> registerShop(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request, Role.USER));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody UserProfileRequest request, Principal principal) {
        // O 'Principal' contém o email do user extraído do Token JWT
        String email = principal.getName();
        return ResponseEntity.ok(service.updateUserProfile(email, request));
    }
}