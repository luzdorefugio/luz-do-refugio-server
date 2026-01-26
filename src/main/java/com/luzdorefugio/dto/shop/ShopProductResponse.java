package com.luzdorefugio.dto.shop;

import java.math.BigDecimal;
import java.util.UUID;

public record ShopProductResponse(
    UUID id,
    String sku,
    String name,
    String description,
    BigDecimal salePrice,
    int stock,
    int weightGrams,
    boolean featured,
    boolean active
) {}