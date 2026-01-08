package com.luzdorefugio.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_stock")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductStock {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;
    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdated;

    // Atualiza a data automaticamente antes de gravar
    @PrePersist @PreUpdate
    public void updateTimestamp() {
        this.lastUpdated = Instant.now();
    }
}