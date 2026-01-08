 CREATE TABLE orders (
     id UUID PRIMARY KEY,
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     channel VARCHAR(50) DEFAULT 'DIRECT',
     customer_name VARCHAR(500) NOT NULL,
     customer_email VARCHAR(500) NOT NULL,
     customer_phone VARCHAR(500),
     customer_nif VARCHAR(500),
     address VARCHAR(500) NOT NULL,
     city VARCHAR(100) NOT NULL,
     zip_code VARCHAR(20) NOT NULL,
     payment_method VARCHAR(50) NOT NULL,
     total_amount DECIMAL(10, 2) NOT NULL,
     status VARCHAR(50) DEFAULT 'PENDING',
     invoice_issued BOOLEAN NOT NULL DEFAULT FALSE,
     applied_promotion_code VARCHAR(50),
     discount_amount DECIMAL(10, 2) DEFAULT 0
);

CREATE TABLE order_items (
                             id UUID PRIMARY KEY,
                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,
                             product_name VARCHAR(255),
                             quantity INTEGER NOT NULL,
                             price DECIMAL(10, 2) NOT NULL,

                             CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
                             CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);