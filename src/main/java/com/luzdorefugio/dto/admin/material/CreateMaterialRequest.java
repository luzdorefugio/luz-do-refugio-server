package com.luzdorefugio.dto.admin.material;

import com.luzdorefugio.domain.enums.MaterialType;
import jakarta.validation.constraints.*; // Validações (Bean Validation)

import java.math.BigDecimal;

public record CreateMaterialRequest(

        @NotBlank(message = "O SKU é obrigatório")
        @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "SKU must contain only uppercase letters, numbers and hyphens")
        String sku,

        @NotBlank(message = "O Nome é obrigatório")
        String name,

        String description,

        // Podes receber String e converter no Service, ou receber Enum direto se o Jackson estiver configurado
        @NotNull(message = "O Tipo é obrigatório")
        MaterialType type,

        @NotBlank(message = "A Unidade é obrigatória")
        String unit,

        @NotNull(message = "O Stock Mínimo é obrigatório")
        @Min(value = 0, message = "O Stock Mínimo não pode ser negativo")
        BigDecimal minStockLevel,

        // --- NOVOS CAMPOS ---

        @Min(value = 0, message = "O Stock Atual não pode ser negativo")
        BigDecimal quantityOnHand,

        @Min(value = 0, message = "O Custo Médio não pode ser negativo")
        BigDecimal averageCost
) {}