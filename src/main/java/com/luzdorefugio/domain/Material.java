package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import com.luzdorefugio.domain.enums.MaterialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator; // Hibernate 6 way

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "materials")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Material extends Auditable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING) // Grava "WAX" na BD em vez de 0
    @Column(nullable = false, length = 20)
    private MaterialType type;

    // Unidade de Medida: "kg", "g", "l", "ml", "un"
    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    @Column(name = "min_stock_level", precision = 19, scale = 4)
    private BigDecimal minStockLevel;

    @Column(name = "avg_cost", precision = 19, scale = 4)
    private BigDecimal averageCost;

    @Column(name = "cost_per_unit")
    private BigDecimal costPerUnit;
    @Column(nullable = false)
    private boolean active;
}