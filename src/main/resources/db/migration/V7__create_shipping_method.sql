CREATE TABLE shipping_methods (
                                  id UUID PRIMARY KEY,
                                  name VARCHAR(100) NOT NULL,
                                  description VARCHAR(255),
                                  price DECIMAL(10, 2) NOT NULL,
                                  active BOOLEAN DEFAULT TRUE,
                                  display_order INT DEFAULT 0,

    -- Colunas de Escalão
                                  min_weight_grams INT DEFAULT 0,      -- Peso mínimo para mostrar este método
                                  max_weight_grams INT DEFAULT 999999, -- Peso máximo (default alto para "infinito")

    -- Mantemos o limiar de oferta
                                  free_shipping_threshold DECIMAL(10, 2) DEFAULT NULL
);

-- INSERIR OS DADOS (Agora com 2 linhas para o Registado)
INSERT INTO shipping_methods
(id, name, description, price, display_order, active, min_weight_grams, max_weight_grams, free_shipping_threshold)
VALUES
    (gen_random_uuid(), 'Correio Registado (100g-500g)', '3-5 dias úteis', 5.04, 1, true, 100, 500, 50.00),
    (gen_random_uuid(), 'Correio Registado (501g-2000g)', '3-5 dias úteis', 8.34, 1, true, 501, 2000, 50.00),
    (gen_random_uuid(), 'Correio Registado (Volume)', '3-5 dias úteis', 15.00, 1, true, 2001, 10000, 100.00),
    (gen_random_uuid(), 'Envio Urgente', 'Entrega 24h', 8.15, 2, true, 0, 999999, NULL),
    (gen_random_uuid(), 'Levantar na sede', 'Gratuito', 0.00, 3, true, 0, 999999, NULL);