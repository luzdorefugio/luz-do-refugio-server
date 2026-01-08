package com.luzdorefugio.domain;

import com.luzdorefugio.domain.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    // Quantidade movida (Positiva ou Negativa)
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "total_value")
    @Builder.Default
    private BigDecimal totalValue = BigDecimal.ZERO;

    // Referência externa (Ex: "PO-12345" ou "ORDER-987")
    @Column(name = "reference_id")
    private String referenceId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "notes")
    private String notes;

    // Quem fez a operação (Ideal para auditoria de segurança)
    @Column(name = "user_id")
    private String userId;

    @PrePersist
    public void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = Instant.now();
        }
    }
}