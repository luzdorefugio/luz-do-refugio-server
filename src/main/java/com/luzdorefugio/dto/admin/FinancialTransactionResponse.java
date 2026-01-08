package com.luzdorefugio.dto.admin;

import com.luzdorefugio.domain.enums.TransactionCategory;
import com.luzdorefugio.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FinancialTransactionResponse(
        UUID id,
        TransactionType type,       // INCOME / EXPENSE
        TransactionCategory category,
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        String referenceId,         // ID da Encomenda ou null
        String createdBy,           // Quem registou (Audit)
        Instant createdAt           // Quando foi registado no sistema
) {}