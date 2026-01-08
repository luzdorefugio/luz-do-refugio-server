CREATE TABLE promotions (
        id UUID PRIMARY KEY,
        code VARCHAR(50) UNIQUE NOT NULL,       -- O código: "NATAL2026", "BEMVINDO10"
        description VARCHAR(255),               -- Para tu saberes o que é: "Promoção de Verão"
        discount_type VARCHAR(20) NOT NULL,     -- PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
        discount_value DECIMAL(10, 2),          -- 10.00 (pode ser 10% ou 10€ dependendo do type)
        special_condition VARCHAR(50),          -- NULL, 'BOGO' (Buy One Get One), 'BUY_2_GET_1'
        min_order_amount DECIMAL(10, 2),        -- Só funciona se gastar > 50€
        max_discount_amount DECIMAL(10, 2),     -- Máximo de desconto (ex: 20% até 15€)
        usage_limit INT,                        -- Quantas vezes pode ser usado no total (ex: 100 primeiros)
        used_count INT DEFAULT 0,               -- Quantas vezes já foi usado
        start_date TIMESTAMP,
        end_date TIMESTAMP,
        active BOOLEAN DEFAULT TRUE,
        created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        updated_at TIMESTAMP WITHOUT TIME ZONE,
        created_by VARCHAR(100),
        last_modified_by VARCHAR(100)
);

INSERT INTO promotions (id,
    code,description,discount_type,discount_value,start_date,end_date,active, created_at, created_by) VALUES (
             gen_random_uuid(),
             'VERAO2026',
             'Promoção de Verão - 10% de desconto em toda a loja',
             'PERCENTAGE',
             10.00,
             '2026-06-21 00:00:00',  -- Começa no início do verão
             '2026-09-23 23:59:59',  -- Termina no fim do verão
             TRUE, NOW(), 'MIGRATION');

-- 2. "ENTREGAGRATIS": Portes Grátis para encomendas acima de 20€
INSERT INTO promotions (id,
                        code,description,discount_type,discount_value,min_order_amount,active, created_at, created_by) VALUES (
             gen_random_uuid(),
             'ENTREGAGRATIS',
             'Oferta de portes em compras superiores a 20€',
             'FREE_SHIPPING',
             0.00,   -- Valor é 0 porque o tipo é envio grátis
             20.00,  -- Condição de ativação
             TRUE, NOW(), 'MIGRATION'
         );

-- 3. "BEMVINDO": 5€ de Desconto (Limitado aos primeiros 100 clientes)
INSERT INTO promotions (id,
                        code,description,discount_type,discount_value,used_count,active, created_at, created_by) VALUES (
             gen_random_uuid(),
             'BEMVINDO',
             'Desconto de boas-vindas de 5€',
             'FIXED_AMOUNT',
             5.00,
             100,    -- Só funciona 100 vezes no total
             TRUE, NOW(), 'MIGRATION'
         );