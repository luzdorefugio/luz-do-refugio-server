package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import com.luzdorefugio.domain.enums.DiscountType;
import com.luzdorefugio.domain.enums.SpecialCondition;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "promotions")
@Data
public class Promotion extends Auditable {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(unique = true, nullable = false)
    private String code; // Ex: PROMO10
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private BigDecimal discountValue; // O valor numérico (10, 15.50, etc)

    @Enumerated(EnumType.STRING)
    private SpecialCondition specialCondition = SpecialCondition.NONE;

    // Restrições
    private BigDecimal minOrderAmount; // Mínimo para ativar
    private Integer usageLimit;        // Limite total de usos
    private Integer usedCount = 0;     // Contador atual

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active = true;

    // Método auxiliar para verificar validade
    public boolean isValid() {
        if (!active) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }
}