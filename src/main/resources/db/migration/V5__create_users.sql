CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    nif VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    zip_code VARCHAR(20),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    active BOOLEAN DEFAULT true
);

INSERT INTO users (id, name, email, password, role, created_at, created_by)
VALUES (gen_random_uuid(),'oliveirala',
   'laura.oliveira@luzdorefugio.pt',
   '$2a$10$uxmEe8UHdmum8FE4wCXVG.2ueJv0H4R7vmPVMIGjjFCJxlJJkDsUC',
   'ADMIN',NOW(),'inicial_script'
);

INSERT INTO users (id, name, email, password, role, created_at, created_by)
VALUES (gen_random_uuid(),   'filipejo',
   'admin@luzdorefugio.pt',
   '$2a$10$uxmEe8UHdmum8FE4wCXVG.2ueJv0H4R7vmPVMIGjjFCJxlJJkDsUC',
   'ADMIN',NOW(),'inicial_script'
);