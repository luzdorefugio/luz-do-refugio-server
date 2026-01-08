package com.luzdorefugio.domain.enums;

public enum MovementType {
    INBOUND,     // Compra (Entrada)
    OUTBOUND,    // Venda/Uso (Saída)
    ADJUSTMENT,  // Correção de Inventário (Quebra/Perda)
    RETURN,      // Devolução de Cliente
    RESERVED     // Alocado para produção
}