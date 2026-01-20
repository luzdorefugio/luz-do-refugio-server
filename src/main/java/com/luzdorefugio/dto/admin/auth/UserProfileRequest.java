package com.luzdorefugio.dto.admin.auth;

public record UserProfileRequest(
        String phone,
        String email,
        String nif,
        String address,
        String city,
        String zipCode
) {}