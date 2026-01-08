package com.luzdorefugio.dto.admin;

import java.math.BigDecimal;
import java.time.Instant;

public record FinancialTransaction(
        Instant date,
        String description, // "Venda: Vela Lavanda" ou "Compra: Cera"
        String type,        // "INCOME" ou "EXPENSE"
        BigDecimal amount   // Valor positivo ou negativo
) {}