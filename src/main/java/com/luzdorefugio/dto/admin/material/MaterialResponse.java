package com.luzdorefugio.dto.admin.material;

import com.luzdorefugio.domain.enums.MaterialType;
import java.math.BigDecimal;
import java.util.UUID;

public record MaterialResponse(
        UUID id,
        String sku,
        String name,
        MaterialType type,
        String unitOfMeasure,
        BigDecimal minStockLevel,
        BigDecimal averageCost,
        BigDecimal quantityOnHand,
        boolean active
) {}