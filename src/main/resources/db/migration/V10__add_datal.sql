
INSERT INTO materials (id, name, type, unit, min_stock_level, avg_cost, sku, created_at, created_by) VALUES
    (gen_random_uuid(), 'Frascos','RECIPIENTE', 'unid', 0, 0.301, 'FRAS-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Copos','RECIPIENTE', 'unid', 0, 0.610, 'COPO-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Taças','RECIPIENTE', 'unid', 0, 0.830, 'TACA-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Pires','RECIPIENTE', 'unid', 0, 0.765, 'PIRE-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Copos de plástico','RECIPIENTE', 'unid', 0, 0.010, 'COPL-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Pavios 12','PAVIOS', 'unid', 0, 0.112, 'PAV12-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Pavios 16','PAVIOS', 'unid', 0, 0.112, 'PAV16-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Pavios 22','PAVIOS', 'unid', 0, 0.112, 'PAV22-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Caixa 10*10*10','EMBALAGEM', 'unid', 0, 0.660, 'CX10-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Caixa 15*15*5','EMBALAGEM', 'unid', 0, 0.285, 'CX15-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Caixa 20*15*10','EMBALAGEM', 'unid', 0, 1.031, 'CX20-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Caixa 13*13*11 branca','EMBALAGEM', 'unid', 0, 0.880, 'CXBR-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Fita cetim bordeaux','EMBALAGEM', 'cm', 0, 0.0002, 'FTCT-BRX', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Cola pavio','UTILS', 'unid', 0, 0.026, 'COLA-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Pétalas vermelhas','UTILS', 'unid', 0, 0.0005, 'PETAL-VERM', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Tampas pretas','LIDS', 'unid', 0, 0.146, 'TPPR-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Tampas douradas','LIDS', 'unid', 0, 0.140, 'TPDO-001', NOW(), 'MIGRATION'),
    (gen_random_uuid(), 'Lacre Bordeaux - 12','LACRE', 'unid', 0, 0.140, 'LACBRX-012', NOW(), 'MIGRATION');

INSERT INTO products (id, name, sku, price, created_at, created_by, burn_time, intensity, top_note, heart_note, base_note) VALUES
(gen_random_uuid(), 'Vela da foz', 'VEL_FOZ', 7, NOW(), 'MIGRATION', '± 25 Horas', 3,
 'Notas de Ozono, Brisa do Mar e Citrinos Frescos', 'Algas Marinhas, Sal e Flores de Água', 'Cedro, Musgo e Almíscar Branco'),
   -- 2. Pistacho Royal (Info adicionada)
(gen_random_uuid(), 'Pistacho royal', 'VEL_PISTACHO', 7, NOW(), 'MIGRATION', '± 25 Horas', 4,
'Suaves, ligeiramente frutadas, frutos secos','Quentes, doces, cremosidade sutil','Doces, envolventes, toque torrado'),

-- 3. Jardim Tutti-Frutti (Sem info -> NULL)
(gen_random_uuid(), 'Jardim Tutti-Frutti', 'VEL_TUTTI', 4, NOW(), 'MIGRATION', '± 20 Horas', 3, NULL, NULL, NULL),

-- 4. Ambrosia Tropical (Info adicionada)
(gen_random_uuid(), 'Ambrosia Tropical', 'VEL_AMBROSIA', 10, NOW(), 'MIGRATION', '± 35 Horas', 4,
'Manga, laranja, limão','Manga, pêssego, frutas vermelhas','Baunilha, canela'),

-- 5. Coração de Luz (Info adicionada)
(gen_random_uuid(), 'Coração de Luz', 'VEL_CORACAO', 8, NOW(), 'MIGRATION', '± 30 Horas', 3,
'Rosa, capim-limão, malagueta','Gerânio, rosa, unha','Rosa, pau-santo'),

(gen_random_uuid(), 'Pinheiro de natal', 'VEL_PINH-NATAL', 10, NOW(), 'MIGRATION', '± 35 Horas', 4, NULL, NULL, NULL),
(gen_random_uuid(), 'Luz do Abrigo', 'VEL_ABRIGO', 8, NOW(), 'MIGRATION', '± 30 Horas', NULL, NULL, NULL, NULL),
(gen_random_uuid(), 'Casamentos', 'VEL_CASAMENTO', 6, NOW(), 'MIGRATION', '± 20 Horas', NULL, NULL, NULL, NULL),
(gen_random_uuid(), 'Pack Promessa: Doce Coração', 'VEL_VALENTI', 18, NOW(), 'MIGRATION', NULL, NULL, NULL, NULL, NULL);

INSERT INTO product_stock (id, quantity_on_hand, product_id) VALUES
     (gen_random_uuid(),11 , (SELECT id FROM products WHERE sku = 'VEL_FOZ')),
     (gen_random_uuid(), 2, (SELECT id FROM products WHERE sku = 'VEL_PISTACHO')),
     (gen_random_uuid(), 0, (SELECT id FROM products WHERE sku = 'VEL_TUTTI')),
     (gen_random_uuid(),15 , (SELECT id FROM products WHERE sku = 'VEL_AMBROSIA')),
     (gen_random_uuid(),2 , (SELECT id FROM products WHERE sku = 'VEL_CORACAO')),
     (gen_random_uuid(),0 , (SELECT id FROM products WHERE sku = 'VEL_PINH-NATAL')),
     (gen_random_uuid(), 0, (SELECT id FROM products WHERE sku = 'VEL_ABRIGO')),
     (gen_random_uuid(),0 , (SELECT id FROM products WHERE sku = 'VEL_CASAMENTO')),
     (gen_random_uuid(), 0, (SELECT id FROM products WHERE sku = 'VEL_VALENTI'));

INSERT INTO product_recipes (id, quantity_required, material_id, product_id) VALUES
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'FRAS-001'),
      (SELECT id FROM products WHERE sku = 'VEL_FOZ')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'PAV12-001'),
      (SELECT id FROM products WHERE sku = 'VEL_FOZ')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'COLA-001'),
      (SELECT id FROM products WHERE sku = 'VEL_FOZ')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'CX10-001'),
      (SELECT id FROM products WHERE sku = 'VEL_FOZ'));

INSERT INTO product_recipes (id, quantity_required, material_id, product_id) VALUES
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'FRAS-001'),
      (SELECT id FROM products WHERE sku = 'VEL_PISTACHO')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'PAV16-001'),
      (SELECT id FROM products WHERE sku = 'VEL_PISTACHO')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'COLA-001'),
      (SELECT id FROM products WHERE sku = 'VEL_PISTACHO')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'CX10-001'),
      (SELECT id FROM products WHERE sku = 'VEL_PISTACHO'));

INSERT INTO product_recipes (id, quantity_required, material_id, product_id) VALUES
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'FRAS-001'),
      (SELECT id FROM products WHERE sku = 'VEL_AMBROSIA')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'PAV22-001'),
      (SELECT id FROM products WHERE sku = 'VEL_AMBROSIA')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'COLA-001'),
      (SELECT id FROM products WHERE sku = 'VEL_AMBROSIA')),
     (gen_random_uuid(),1,
      (SELECT id FROM materials WHERE sku = 'CX10-001'),
      (SELECT id FROM products WHERE sku = 'VEL_AMBROSIA'));

INSERT INTO product_recipes (id, quantity_required, material_id, product_id) VALUES
(gen_random_uuid(),2,
    (SELECT id FROM materials WHERE sku = 'PAV16-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),2,
    (SELECT id FROM materials WHERE sku = 'COLA-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'TPDO-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'FRAS-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),160,
    (SELECT id FROM materials WHERE sku = 'FTCT-BRX'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),20,
    (SELECT id FROM materials WHERE sku = 'PETAL-VERM'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'CX20-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'TACA-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI'));

UPDATE products p SET estimated_cost = (
    SELECT COALESCE(SUM(pr.quantity_required * m.avg_cost), 0)
    FROM product_recipes pr
             JOIN materials m ON pr.material_id = m.id
    WHERE pr.product_id = p.id
);

INSERT INTO stock (id, quantity_on_hand, material_id, created_at, created_by) VALUES
    (gen_random_uuid(),110,  (SELECT id FROM materials WHERE sku = 'FRAS-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),160,  (SELECT id FROM materials WHERE sku = 'COPO-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),96,  (SELECT id FROM materials WHERE sku = 'TACA-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),47,  (SELECT id FROM materials WHERE sku = 'PIRE-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),93,  (SELECT id FROM materials WHERE sku = 'COPL-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),825,  (SELECT id FROM materials WHERE sku = 'PAV12-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),916,  (SELECT id FROM materials WHERE sku = 'PAV16-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),0,  (SELECT id FROM materials WHERE sku = 'PAV22-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),40,  (SELECT id FROM materials WHERE sku = 'CX10-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),170,  (SELECT id FROM materials WHERE sku = 'CX15-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),30,  (SELECT id FROM materials WHERE sku = 'CX20-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),91,  (SELECT id FROM materials WHERE sku = 'CXBR-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),3300,  (SELECT id FROM materials WHERE sku = 'FTCT-BRX'), NOW(), 'MIGRATION'),
    (gen_random_uuid(),60,  (SELECT id FROM materials WHERE sku = 'COLA-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(), 2000, (SELECT id FROM materials WHERE sku = 'PETAL-VERM'), NOW(), 'MIGRATION'),
    (gen_random_uuid(), 48, (SELECT id FROM materials WHERE sku = 'TPPR-001'), NOW(), 'MIGRATION'),
    (gen_random_uuid(), 60, (SELECT id FROM materials WHERE sku = 'TPDO-001'), NOW(), 'MIGRATION');
