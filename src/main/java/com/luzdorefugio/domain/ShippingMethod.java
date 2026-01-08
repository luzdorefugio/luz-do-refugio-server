package com.luzdorefugio.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shipping_methods")
@Data
public class ShippingMethod {
    @Id
    @UuidGenerator
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active = true;
    @Column
    private Integer displayOrder;
}