package com.luzdorefugio.repository;

import com.luzdorefugio.domain.FinancialTransaction;
import com.luzdorefugio.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface FinancialRepository extends JpaRepository<FinancialTransaction, UUID> {

    // Soma total por tipo num intervalo de datas (Ex: Lucro deste mês)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM FinancialTransaction t " +
            "WHERE t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByTypeAndDateRange(@Param("type") TransactionType type,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
}