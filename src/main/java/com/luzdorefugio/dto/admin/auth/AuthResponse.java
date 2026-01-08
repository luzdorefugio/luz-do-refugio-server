package com.luzdorefugio.dto.admin.auth;

public record AuthResponse(
        String token,
        String role,
        String name,
        String phone,
        String nif,
        String address,
        String city,
        String zipCode
) {}