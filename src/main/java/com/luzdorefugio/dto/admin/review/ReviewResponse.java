package com.luzdorefugio.dto.admin.review;

import java.util.UUID;

public record ReviewResponse(
        UUID id,
        String authorName,
        String content,
        Integer rating,
        boolean active
) {}