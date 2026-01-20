CREATE TABLE email_quota (
    data VARCHAR(20) NOT NULL PRIMARY KEY,  -- A chave é a data em texto (ex: "2026-01-09")
    quantidade INTEGER DEFAULT 0            -- Quantos emails já foram enviados hoje
);

CREATE TABLE pending_email (
    id UUID PRIMARY KEY,                -- Cria ID automático (1, 2, 3...)
    para VARCHAR(255) NOT NULL,           -- Email do cliente
    assunto VARCHAR(255),                 -- Assunto do email
    texto TEXT,                           -- O corpo do email (TEXT aguenta HTML grande)
    data_criacao TIMESTAMP DEFAULT NOW()  -- Guarda a hora em que o erro aconteceu
);

CREATE TABLE contact_messages (
          id UUID PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          email VARCHAR(255) NOT NULL,
          message TEXT NOT NULL,
          is_read BOOLEAN DEFAULT FALSE,
          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
          updated_at TIMESTAMP WITHOUT TIME ZONE,
          created_by VARCHAR(100),
          last_modified_by VARCHAR(100)
);