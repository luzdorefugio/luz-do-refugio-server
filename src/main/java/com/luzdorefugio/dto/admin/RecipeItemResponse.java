package com.luzdorefugio.dto.admin;

import java.math.BigDecimal;
import java.util.UUID;

public record RecipeItemResponse(
        UUID materialId,
        String materialName,  // Já mandamos o nome para o Angular não ter de procurar!
        BigDecimal quantity,
        BigDecimal costPerUnit,
        String unit
) {}