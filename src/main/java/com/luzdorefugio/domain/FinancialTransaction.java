package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import com.luzdorefugio.domain.enums.TransactionCategory;
import com.luzdorefugio.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "financial_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FinancialTransaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    @Column(nullable = false)
    private BigDecimal amount; // Valor absoluto

    private String description;

    @Column(nullable = false)
    private LocalDate transactionDate;

    // Opcional: Para ligar à Encomenda ou ao StockMovement
    private String referenceId;
}