package com.luzdorefugio.dto.admin;

import com.luzdorefugio.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        String materialName, // Vamos enviar o nome já resolvido
        MovementType type,
        BigDecimal quantity,
        String referenceId,
        String notes,
        Instant timestamp
) {}