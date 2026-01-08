package com.luzdorefugio.dto.admin;

import com.luzdorefugio.domain.enums.OrderChannel;
import java.math.BigDecimal;

public record SalesByChannelDTO(
        OrderChannel channel,
        BigDecimal totalValue,
        Long count
) {}