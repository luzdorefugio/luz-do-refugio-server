package com.luzdorefugio.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BulkPurchaseRequest(
        String supplier,          // "Gran Velada"
        BigDecimal shippingCost,  // 5.90€
        List<ItemPurchase> items  // A lista de produtos
) {
    public record ItemPurchase(
            UUID materialId,
            BigDecimal quantity,  // 5kg
            BigDecimal totalCost  // 20€ (pela quantidade toda)
    ) {}
}