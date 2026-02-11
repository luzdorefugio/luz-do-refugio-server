package com.luzdorefugio.service;

import com.luzdorefugio.domain.User;
import com.luzdorefugio.dto.admin.UserRequest;
import com.luzdorefugio.dto.admin.UserResponse;
import com.luzdorefugio.repository.UserRepository;
import com.luzdorefugio.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserRequest userData) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

        user.setName(userData.getName());
        user.setEmail(userData.getEmail());
        user.setRole(userData.getRole());

        // Só altera a password se ela for enviada (não estiver vazia)
        if (userData.getPassword() != null && !userData.getPassword().isBlank()) {
            // Lembra-te de encriptar aqui se não tiveres um @PreUpdate no domínio
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        return mapToResponse(repository.save(user));
    }

    @Transactional
    public void softDelete(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

        user.setActive(false); // Soft Delete
        repository.save(user);
    }

    public long getTotalActiveUsers() {
        return repository.countByActiveTrue();
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .active(user.isActive())
                .ordersCount(0)
                .build();
    }
}