/* V1__init_schema.sql
   Criação inicial das tabelas de Inventário
   Data: 16-12-2025
   Autor: Joao Filipe
*/

-- Habilitar extensão para gerar UUIDs v4 caso o Java não envie
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabela de Materiais (Definições)
CREATE TABLE materials (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    sku VARCHAR(50) NOT NULL UNIQUE, -- O código de barras/referência interna
    type VARCHAR(20) NOT NULL, -- ENUM: WAX, WICK, FRAGRANCE...
    unit VARCHAR(10), -- kg, un, l
    min_stock_level NUMERIC(19, 4), -- Alerta de stock baixo
    avg_cost NUMERIC(19, 4),
    cost_per_unit NUMERIC(19, 4) DEFAULT 0.00,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    active BOOLEAN DEFAULT true
);

-- 2. Tabela de Stock (Estado Atual)
CREATE TABLE stock (
    id UUID PRIMARY KEY,
    material_id UUID NOT NULL UNIQUE, -- 1 Material = 1 Registo de Stock
    quantity_on_hand NUMERIC(19, 4) NOT NULL DEFAULT 0,
    quantity_allocated NUMERIC(19, 4) NOT NULL DEFAULT 0, -- Reservado para produção
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),

    CONSTRAINT fk_stock_material FOREIGN KEY (material_id) REFERENCES materials(id)
);

-- 3. Tabela de Movimentos (Histórico/Auditoria)
CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    material_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL, -- INBOUND, OUTBOUND, ADJUSTMENT...
    quantity NUMERIC(19, 4) NOT NULL, -- Pode ser negativo para saídas
    total_value NUMERIC(19, 2) NOT NULL DEFAULT 0,
    reference_id VARCHAR(100), -- ID da Ordem de Compra ou Fabrico
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(100), -- Quem fez a operação (pode vir do Keycloak)
    notes TEXT,
    CONSTRAINT fk_movement_material FOREIGN KEY (material_id) REFERENCES materials(id)
);

-- Índices para performance (Um Team Lead preocupa-se com queries lentas!)
CREATE INDEX idx_stock_movements_material_date ON stock_movements(material_id, timestamp);
CREATE INDEX idx_materials_sku ON materials(sku);