package com.luzdorefugio.dto.order;

import com.luzdorefugio.domain.enums.OrderChannel;
import com.luzdorefugio.domain.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    // Agora tem sub-objetos estruturados
    private CustomerData customer;
    private GiftDetails giftDetails; // Novo campo opcional
    private PaymentData payment;
    private List<OrderItemDto> items;

    private BigDecimal total;
    private OrderChannel channel;
    private OrderStatus status;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private String appliedPromotionCode;
    private BigDecimal discountAmount;

    // Mantemos estes para compatibilidade ou uso interno
    private Boolean withoutBox;
    private Boolean withoutCard;
    private Boolean invoiceIssued;

    @Data
    public static class CustomerData {
        private String fullName;
        private String email;
        private String phone;
        private String nif;

        // Moradas separadas
        private AddressData shippingAddress;
        private AddressData billingAddress;
    }

    @Data
    public static class AddressData {
        private String street;
        private String city;
        private String zip;
        private String country;
    }

    @Data
    public static class GiftDetails {
        private Boolean isGift;
        private String fromName;
        private String toName;
        private String message;
    }

    @Data
    public static class PaymentData {
        private String method; // No frontend mudámos para 'method', antes era 'paymentMethod'
    }

    @Data
    public static class OrderItemDto {
        private UUID productId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
    }
}