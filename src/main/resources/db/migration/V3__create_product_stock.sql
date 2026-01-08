CREATE TABLE product_stock (
                               id UUID PRIMARY KEY,
                               product_id UUID NOT NULL UNIQUE,
                               quantity_on_hand INTEGER NOT NULL DEFAULT 0,
                               last_updated TIMESTAMP WITH TIME ZONE,
                               CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id)
);