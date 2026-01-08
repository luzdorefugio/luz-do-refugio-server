package com.luzdorefugio.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialType {

    // Matérias-Primas Principais
    CERA("Cera"),
    PAVIOS("Pavio"),
    FRAGRANCE("Essência"),
    ADDITIVE("Aditivo"),
    // Componentes Físicos
    RECIPIENTE("Copos/Frascos"),
    LIDS("Tampas"),
    EMBALAGEM("Caixas/Etiquetas"),
    LACRE("Lacre"),
    // Outros Custos
    PORTES("Portes de Envio"),
    UTILS("Utensílios/Ferramentas");

    private final String description;
}