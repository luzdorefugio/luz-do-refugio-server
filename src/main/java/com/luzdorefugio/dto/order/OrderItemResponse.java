package com.luzdorefugio.dto.order;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
