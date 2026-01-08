package com.luzdorefugio.dto.admin.auth;

public record RegisterRequest(
        String name,
        String email,
        String password
) {}