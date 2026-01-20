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
   ('Inês Rosa', 'Produtos excelentes, de extrema qualidade! Nota-se o carinho com que são feitos! Muito atenciosos!', 5, true, NOW(), 'MIGRATION'),
   ('Isabel Rodrigues', 'Velas lindas e cheirosas, cheiros suaves ou mais intensos, que deixam um cheirinho óptimo em casa. E fica sempre para uma decoração linda :)', 5, true, NOW(), 'MIGRATION'),
   ('Joana Alves', 'Super simpáticos e cuidadosos Tive uma experiência fantástica Comprei a quase 1 ano e as velas mantém o mesmo cheirinho bom Recomendo a todos 🥹', 5, true, NOW(), 'MIGRATION');