package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Stock extends Auditable {

    @Id
    @UuidGenerator
    private UUID id;

    // Relacionamento 1 para 1: Cada material tem apenas 1 registo de stock
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", unique = true, nullable = false)
    private Material material;

    // O que está fisicamente na prateleira
    @Column(name = "quantity_on_hand", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityOnHand;

    // O que está reservado para encomendas/produção
    @Column(name = "quantity_allocated", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityAllocated;
}