package com.luzdorefugio.dto.admin.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateReviewRequest(
        UUID id,
        String authorName,
        String content,
        Integer rating,
        boolean active
) {}