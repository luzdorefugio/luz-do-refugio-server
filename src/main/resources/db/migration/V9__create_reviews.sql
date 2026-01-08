-- 1. Criar Tabela
CREATE TABLE reviews (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     author_name VARCHAR(255) NOT NULL,
     content TEXT NOT NULL,
     rating INTEGER DEFAULT 5,
     active BOOLEAN DEFAULT TRUE,
     created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     updated_at TIMESTAMP WITHOUT TIME ZONE,
     created_by VARCHAR(100),
     last_modified_by VARCHAR(100)
);

-- 2. Inserir Reviews de Teste
INSERT INTO reviews (author_name, content, rating, active, created_at, created_by) VALUES
   ('Ana Martins', 'O cheiro da vela de Pistacho é divinal! Encheu a sala toda. Recomendo muito.', 5, true, NOW(), 'MIGRATION'),
   ('Carlos Silva', 'A embalagem é super cuidada, nota-se o carinho. O lacre dá um toque especial.', 5, true, NOW(), 'MIGRATION'),
   ('Mariana Costa', 'Comprei para oferecer e a minha amiga adorou. Entrega super rápida.', 5, true, NOW(), 'MIGRATION'),
   ('Sofia R.', 'A vela da Foz traz mesmo memórias do mar. Muito fresca.', 4, true, NOW(), 'MIGRATION'),
   ('Beatriz Neves', 'Já é a segunda vez que encomendo. A qualidade é top e duram imenso tempo.', 5, true, NOW(), 'MIGRATION');