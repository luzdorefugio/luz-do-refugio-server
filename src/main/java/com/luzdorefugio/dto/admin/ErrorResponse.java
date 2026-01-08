package com.luzdorefugio.dto.admin;

import java.time.Instant;

// Record é perfeito para isto (imutável e rápido)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}