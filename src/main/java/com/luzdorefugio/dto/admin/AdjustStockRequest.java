package com.luzdorefugio.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record AdjustStockRequest(
        @NotNull UUID materialId,
        @Positive(message = "A quantidade tem de ser positiva") BigDecimal quantity,
        String reason
) {}