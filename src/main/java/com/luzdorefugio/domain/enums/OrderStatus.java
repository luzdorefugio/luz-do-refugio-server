package com.luzdorefugio.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pendente"),
    PAID("Pago"),
    SHIPPED("Enviado"),
    DELIVERED("Entregue"),
    CANCELLED("Cancelado"),
    RETURNED("Devolvido"); // O tal novo estado

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

}