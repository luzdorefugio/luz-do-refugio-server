package com.luzdorefugio.dto.admin.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank(message = "SKU is required")
        String sku,
        @NotBlank(message = "Name is required")
        String name,
        @PositiveOrZero
        BigDecimal price,
        String burnTime,
        Integer intensity,
        String topNote,
        String heartNote,
        String baseNote,
        @NotEmpty(message = "Product must have at least one ingredient")
        @Valid
        List<ProductRecipeRequest> recipeItems,
        boolean activeShop
) {
    // Record interno para os itens da receita (Cera, Pavio, etc.)
    public record ProductRecipeRequest(
            @NotNull UUID materialId,
            @Positive(message = "Quantity must be greater than 0") BigDecimal quantity
    ) {}
}