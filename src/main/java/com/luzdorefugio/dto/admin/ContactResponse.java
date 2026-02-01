package com.luzdorefugio.dto.admin;

import java.time.Instant;
import java.util.UUID;

public record ContactResponse(
    UUID id,
    String name,
    String email,
    String message,
    boolean read,
    String createdBy,
    Instant createdAt
) {}