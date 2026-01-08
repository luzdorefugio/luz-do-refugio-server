package com.luzdorefugio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_recipes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductRecipe {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // Quanto gasta para fazer 1 unidade? Ex: 0.2 (kg)
    @Column(name = "quantity_required", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityRequired;
}