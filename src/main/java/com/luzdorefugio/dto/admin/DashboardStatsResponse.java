package com.luzdorefugio.dto.admin;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private BigDecimal totalInvestedMaterials; // Valor em Matéria-Prima
    private BigDecimal potentialRevenue;       // Valor de Venda (Stock Velas)
    private BigDecimal estimatedProfit;        // Lucro Estimado (Venda - Custo)
}