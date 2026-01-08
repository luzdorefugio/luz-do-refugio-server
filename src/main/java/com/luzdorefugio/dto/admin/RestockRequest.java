package com.luzdorefugio.dto.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record RestockRequest(
        UUID materialId,
        BigDecimal quantity,    // Quanto compraste (ex: 10kg)
        BigDecimal totalCost,   // Quanto pagaste na fatura (ex: 50.00€)
        String supplier         // Fornecedor (Opcional, vai para as notas)
) {}