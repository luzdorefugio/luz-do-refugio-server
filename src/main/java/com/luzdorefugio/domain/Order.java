package com.luzdorefugio.domain;

import com.luzdorefugio.domain.enums.OrderChannel;
import com.luzdorefugio.domain.enums.OrderStatus;
import com.luzdorefugio.security.StringEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @UuidGenerator
    private UUID id;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private OrderChannel channel;
    @Convert(converter = StringEncryptor.class)
    private String customerName;
    @Convert(converter = StringEncryptor.class)
    private String customerEmail;
    @Convert(converter = StringEncryptor.class)
    private String customerPhone;
    @Convert(converter = StringEncryptor.class)
    private String customerNif;
    @Convert(converter = StringEncryptor.class)
    private String address;
    private String city;
    private String zipCode;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private String appliedPromotionCode;
    private BigDecimal discountAmount;
    @Builder.Default
    private boolean invoiceIssued = false;

    // --- Pagamento ---
    private String paymentMethod;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // --- Itens (A magia acontece aqui) ---
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}