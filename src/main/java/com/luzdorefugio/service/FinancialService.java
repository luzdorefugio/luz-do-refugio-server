package com.luzdorefugio.service;

import com.luzdorefugio.domain.FinancialTransaction;
import com.luzdorefugio.domain.enums.TransactionCategory;
import com.luzdorefugio.domain.enums.TransactionType;
import com.luzdorefugio.dto.admin.FinancialTransactionResponse;
import com.luzdorefugio.repository.FinancialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialService {
    private final FinancialRepository repository;

    public List<FinancialTransactionResponse> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "transactionDate"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void registerRevenue(BigDecimal amount, String orderId, String customerName) {
        FinancialTransaction tx = FinancialTransaction.builder()
                .type(TransactionType.INCOME)
                .category(TransactionCategory.PRODUCT_SALE)
                .amount(amount)
                .description("Venda #" + orderId + " - " + customerName)
                .referenceId(orderId)
                .transactionDate(LocalDate.now())
                .build();
        repository.save(tx);
    }

    // Registar Despesa de Material (Chamado quando dás entrada de stock pago)
    @Transactional
    public void registerExpense(BigDecimal amount, TransactionCategory category, String description) {
        FinancialTransaction tx = FinancialTransaction.builder()
                .type(TransactionType.EXPENSE)
                .category(category)
                .amount(amount)
                .description(description)
                .transactionDate(LocalDate.now())
                .build();
        repository.save(tx);
    }

    @Transactional
    public void registerRefund(BigDecimal amount, String orderId, String customerName) {
        FinancialTransaction tx = FinancialTransaction.builder()
                .type(TransactionType.EXPENSE)       // <--- TIPO DESPESA
                .category(TransactionCategory.REFUND)  // <--- CATEGORIA REEMBOLSO
                .amount(amount)
                .description("Reembolso/Cancelamento #" + orderId + " - " + customerName)
                .referenceId(orderId)
                .transactionDate(LocalDate.now())
                .build();

        repository.save(tx);
    }

    // Dashboard: Balanço do Mês
    public BigDecimal getMonthlyBalance(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal income = repository.sumByTypeAndDateRange(TransactionType.INCOME, start, end);
        BigDecimal expense = repository.sumByTypeAndDateRange(TransactionType.EXPENSE, start, end);

        return income.subtract(expense);
    }

    private FinancialTransactionResponse mapToResponse(FinancialTransaction tx) {
        return new FinancialTransactionResponse(
                tx.getId(),
                tx.getType(),
                tx.getCategory(),
                tx.getAmount(),
                tx.getDescription(),
                tx.getTransactionDate(),
                tx.getReferenceId(),
                tx.getCreatedBy(),  // Vem da classe Auditable
                tx.getCreatedAt()   // Vem da classe Auditable
        );
    }
}