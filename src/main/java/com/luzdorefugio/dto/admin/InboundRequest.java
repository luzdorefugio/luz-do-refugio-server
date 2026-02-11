package com.luzdorefugio.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record InboundRequest(
        @NotNull UUID materialId,
        @Positive BigDecimal quantity, // Quanto entrou (ex: 50.0
        @Positive BigDecimal unitCost, // Quanto custou por unidade (ex: 4.50€/kg)
        String purchaseOrder // Referência da fatura/encomenda (ex: "PO-2025-001")
) {}