package com.luzdorefugio.dto.admin.review;

import java.util.UUID;

public record CreateReviewRequest(
        UUID id,
        String authorName,
        String content,
        Integer rating,
        boolean active
) {}