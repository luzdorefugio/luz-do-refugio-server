package com.luzdorefugio.dto.order;

import com.luzdorefugio.domain.enums.OrderChannel;
import com.luzdorefugio.domain.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private CustomerData customer;
    private PaymentData payment;
    private List<OrderItemDto> items;
    private BigDecimal total;
    private OrderChannel channel;
    private String shippingMethod;
    private OrderStatus status;
    private BigDecimal shippingCost;
    private String appliedPromotionCode;
    private BigDecimal discountAmount;
    private Boolean withoutBox;
    private Boolean withoutCard;
    private Boolean invoiceIssued;

    @Data
    public static class PaymentData {
        private String paymentMethod;
    }

    @Data
    public static class OrderItemDto {
        private UUID productId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
    }
}