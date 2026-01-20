package com.luzdorefugio.dto.admin.product;

import com.luzdorefugio.dto.admin.RecipeItemResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AdminProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        String cardMessage,
        String cardColorDesc,
        BigDecimal price,
        int maxProduction,
        int stock,
        List<RecipeItemResponse> recipeItems,
        BigDecimal estimatedCost,
        String burnTime,
        Integer intensity,
        String topNote,
        String heartNote,
        String baseNote,
        boolean activeShop,
        boolean active
) {}