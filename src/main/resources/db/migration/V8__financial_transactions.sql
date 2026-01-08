CREATE TABLE financial_transactions (
        id UUID PRIMARY KEY,
        type VARCHAR(20) NOT NULL,          -- 'INCOME' ou 'EXPENSE'
        category VARCHAR(50) NOT NULL,      -- 'PRODUCT_SALE', 'MATERIAL_PURCHASE', etc.
        amount DECIMAL(19, 2) NOT NULL,     -- Dinheiro: 19 dígitos no total, 2 casas decimais
        description VARCHAR(255),
        transaction_date DATE NOT NULL,     -- Data contável (sem horas)
        reference_id VARCHAR(100),          -- Opcional: ID da Encomenda ou Lote de Produção
        created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        updated_at TIMESTAMP WITHOUT TIME ZONE,
        created_by VARCHAR(100),
        last_modified_by VARCHAR(100)
);

CREATE INDEX idx_financial_date ON financial_transactions(transaction_date);

CREATE INDEX idx_financial_type ON financial_transactions(type);

CREATE INDEX idx_financial_reference ON financial_transactions(reference_id);