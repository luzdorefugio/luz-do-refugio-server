package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product extends Auditable {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private String cardMessage;
    private String cardColorDesc;
    private Integer weightGrams;
    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;
    @Column(name = "estimated_cost", precision = 19, scale = 4)
    private BigDecimal estimatedCost;
    private String burnTime;
    private Integer intensity;
    @Embedded
    private ScentProfile scentProfile;
    @Column(nullable = false)
    private boolean activeShop;
    @Column(nullable = false)
    private boolean active;
    @Column(nullable = false)
    private boolean featured;
    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductRecipe> recipe = new ArrayList<>();

    public void addRecipeItem(Material material, BigDecimal quantityRequired) {
        ProductRecipe item = ProductRecipe.builder()
                .product(this)
                .material(material)
                .quantityRequired(quantityRequired)
                .build();
        recipe.add(item);
    }
}