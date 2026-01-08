package com.luzdorefugio.dto.admin.material;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MaterialPurchaseRequest(
        @NotNull @DecimalMin("0.01") BigDecimal quantity,   // Quanto compraste (ex: 50 kg)
        @NotNull @DecimalMin("0.01") BigDecimal totalCost,  // Quanto custou no total (ex: 200€)
        String supplierNote                                 // Ex: "Fornecedor Gran Velada"
) {}