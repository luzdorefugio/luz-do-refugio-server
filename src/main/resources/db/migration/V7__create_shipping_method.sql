-- 1. Tabela de Métodos de Envio
CREATE TABLE shipping_methods (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,          -- Ex: Correio Registado
    description VARCHAR(255),            -- Ex: Entrega em 3-5 dias úteis
    price DECIMAL(10, 2) NOT NULL,       -- Ex: 3.50
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0          -- Para escolheres qual aparece primeiro
);


ALTER TABLE orders
    ADD COLUMN shipping_method VARCHAR(100),
    ADD COLUMN shipping_cost DECIMAL(10, 2) DEFAULT 0;

-- 3. Inserir os dados iniciais (os que tinhas hardcoded)
INSERT INTO shipping_methods (id, name, description, price, display_order, active) VALUES
   (gen_random_uuid(), 'Correio Registado', '3-5 dias úteis', 3.50, 1, true),
   (gen_random_uuid(), 'Envio Urgente', 'Entrega 24h', 6.90, 2, true),
   (gen_random_uuid(), 'Levantar no Atelier', 'Gratuito', 0.00, 3, true);