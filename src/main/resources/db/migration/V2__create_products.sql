CREATE TABLE products (
    id UUID PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(19, 2),
    estimated_cost NUMERIC(19, 4),
    burn_time VARCHAR(50),
    intensity INTEGER,
    top_note VARCHAR(255),
    heart_note VARCHAR(255),
    base_note VARCHAR(255),
    card_message VARCHAR(255),
    card_color_desc VARCHAR(255),
    weight_grams int,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    active_shop BOOLEAN DEFAULT false,
    featured BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true
);

CREATE TABLE product_recipes (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    material_id UUID NOT NULL,
    quantity_required NUMERIC(19, 4) NOT NULL,
    CONSTRAINT fk_recipe_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_recipe_material FOREIGN KEY (material_id) REFERENCES materials(id)
);

CREATE INDEX idx_recipe_product ON product_recipes(product_id);