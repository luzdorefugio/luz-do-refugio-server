package com.luzdorefugio.dto.order;

import com.luzdorefugio.domain.enums.OrderChannel;
import com.luzdorefugio.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderFullResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerNif;
    private String address;
    private String city;
    private String zipCode;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private OrderChannel channel;
    private boolean invoiceIssued;
    private List<OrderItemResponse> items;
    private String paymentMethod;
}