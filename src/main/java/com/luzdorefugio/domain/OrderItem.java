package com.luzdorefugio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @UuidGenerator
    private UUID id;

    // Relação com a Encomenda Principal
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore // Para evitar loop infinito no JSON
    private Order order;

    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price; // Preço unitário
}