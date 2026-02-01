package com.luzdorefugio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class PendingEmail {
    @Id
    @UuidGenerator
    private UUID id;

    private String para;
    private String assunto;
    @Column(length = 5000) // Texto longo
    private String texto;
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Construtores, Getters e Setters
    public PendingEmail(String para, String assunto, String texto) {
        this.para = para;
        this.assunto = assunto;
        this.texto = texto;
    }
}