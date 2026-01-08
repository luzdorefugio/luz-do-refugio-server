package com.luzdorefugio.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Embeddable
public class ScentProfile {
    private String topNote;    // Notas de Saída
    private String heartNote;  // Notas de Coração
    private String baseNote;   // Notas de Fundo

}