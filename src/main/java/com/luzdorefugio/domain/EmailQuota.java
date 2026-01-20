package com.luzdorefugio.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EmailQuota {

    @Id
    private String data; // Vamos guardar como "2024-01-09"
    private int quantidade;

    public EmailQuota() {}

    public EmailQuota(String data, int quantidade) {
        this.data = data;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public void incrementar() {
        this.quantidade++;
    }
}