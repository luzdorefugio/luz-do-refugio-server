CREATE TABLE orders (
                        id UUID PRIMARY KEY,

    -- Dados do Cliente / Destinatário
                        customer_name VARCHAR(500) NOT NULL,
                        customer_email VARCHAR(500) NOT NULL,
                        customer_phone VARCHAR(500),
                        customer_nif VARCHAR(500),

    -- Morada de ENVIO (Mantemos os nomes originais para não partir código antigo)
                        address VARCHAR(500) NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        zip_code VARCHAR(20) NOT NULL,

    -- NOVO: Morada de FATURAÇÃO (Se for diferente)
                        billing_address VARCHAR(500),
                        billing_city VARCHAR(100),
                        billing_zip_code VARCHAR(20),

    -- Pagamento e Totais
                        payment_method VARCHAR(50) NOT NULL,
                        total_amount DECIMAL(10, 2) NOT NULL,

    -- NOVO: Detalhes de Logística
                        shipping_method VARCHAR(100),
                        shipping_cost DECIMAL(10, 2) DEFAULT 0,

    -- Estado
                        channel VARCHAR(50) DEFAULT 'DIRECT',
                        status VARCHAR(50) DEFAULT 'PENDING',
                        invoice_issued BOOLEAN NOT NULL DEFAULT FALSE,

    -- Promoções
                        applied_promotion_code VARCHAR(50),
                        discount_amount DECIMAL(10, 2) DEFAULT 0,

    -- NOVO: Detalhes de Oferta (Gift)
                        is_gift BOOLEAN DEFAULT FALSE,
                        gift_message VARCHAR(500),     -- Para a dedicatória (Front tem limite 250)
                        gift_from_name VARCHAR(255),   -- Quem oferece
                        gift_to_name VARCHAR(255),
                        created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                        updated_at TIMESTAMP WITHOUT TIME ZONE,
                        created_by VARCHAR(100),
                        last_modified_by VARCHAR(100)
);

CREATE TABLE order_items (
                             id UUID PRIMARY KEY,
                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,
                             quantity INTEGER NOT NULL,
                             price DECIMAL(10, 2) NOT NULL,

                             CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
                             CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);