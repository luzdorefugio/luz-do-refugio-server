package com.luzdorefugio.dto.admin.auth;

public record LoginRequest(
        String email,
        String password
) {}