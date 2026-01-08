package com.luzdorefugio.service;

import com.luzdorefugio.domain.enums.MaterialType;
import com.luzdorefugio.domain.enums.OrderStatus;
import com.luzdorefugio.dto.admin.EnumResponse;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class EnumService {

    public List<EnumResponse> getMaterialTypes() {
        return Arrays.stream(MaterialType.values())
                .map(type -> new EnumResponse(type.name(), type.getDescription()))
                .toList();
    }

    public List<EnumResponse> getOrderStatus() {
        return Arrays.stream(OrderStatus.values())
                .map(type -> new EnumResponse(type.name(), type.name())) // Se UnitType não tiver descrição, usa o name
                .toList();
    }
}