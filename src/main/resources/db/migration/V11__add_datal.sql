
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

INSERT INTO products (
    id,
    name,
    sku,
    price,
    description, -- Coluna Nova
    created_at,
    created_by,
    burn_time,
    intensity,
    top_note,
    heart_note,
    base_note,
    card_message,
    card_color_desc,
    weight_grams,
    active_shop,
    featured
) VALUES
      (
          gen_random_uuid(),
          'Vela da foz',
          'VEL_FOZ',
          8,
          'Inspirada na beleza indomável da Foz do Arelho, esta vela captura a essência pura do oceano. Ao acendê-la, o espaço é inundado por notas frescas de ozono e citrinos vibrantes. A escolha perfeita para purificar o ar e trazer serenidade.',
          NOW(), 'MIGRATION', '± 25 Horas', 3,
          'Notas de Ozono, Brisa do Mar e Citrinos Frescos',
          'Algas Marinhas, Sal e Flores de Água',
          'Cedro, Musgo e Almíscar Branco',
       'Traga a calma da praia para o seu refúgio. Que esta chama lhe dê conforto e envolva o seu cantinho num aroma especial.',
       'O tom azul representa o vasto oceano. O dourado é a areia. E o branco é o céu que liga tudo com o cheiro do mar.',
       240,
          true, false
      ),
      (
          gen_random_uuid(),
          'Pistacho royal',
          'VEL_PISTACHO',
          8,
          'Uma verdadeira sobremesa olfativa que não enjoa, apenas conforta. Combina a riqueza dos frutos secos com uma cremosidade subtil que aquece a alma. Ideal para acompanhar um dia de chuva ou um café a dois.',
          NOW(), 'MIGRATION', '± 25 Horas', 4,
          'Suaves, ligeiramente frutadas, frutos secos',
          'Quentes, doces, cremosidade sutil',
          'Doces, envolventes, toque torrado',
       'Uma fusão requintada de pistacho e chocolate que envolve o seu refúgio num aroma quente e acolhedor.',
       'O castanho representa o chocolate, branco um delicioso chantilly e por cima uma calda de creme pistacho com dois bocados de chocolate.',
       240,
          true, false
      ),
      (
          gen_random_uuid(),
          'Ambrosia Tropical',
          'VEL_AMBROSIA',
          12,
          'Feche os olhos e viaje para um paraíso tropical. Uma explosão de alegria liderada pela manga suculenta, com um fundo surpreendente de baunilha e canela que transforma a frescura frutada numa fragrância envolvente.',
          NOW(), 'MIGRATION', '± 35 Horas', 4,
          'Manga, laranja, limão',
          'Manga, pêssego, frutas vermelhas',
          'Baunilha, canela',
          'Uma fusão exótica de manga e papaia que envolve o seu refúgio num aroma leve e luminoso.',
          'O amarelo da polpa de manga com o seu granizado e por cima um pedaço de manga e uma folha de hortelã para dar um toque de frescura e depois um topping de papaia em cima.',
       410,
          true, false
      ),
      (
          gen_random_uuid(),
          'Coração de Luz',
          'VEL_CORACAO',
          10,
          'Ritual de amor e misticismo. A rosa bulgaria une-se à malagueta, capim-limão e Pau-Santo. Ideal para o autocuidado e os momentos de introspecção. ❤️ Vela Solidária: 1€ reverte para o Hospital Santa Cruz.',
          NOW(), 'MIGRATION', '± 30 Horas', 3,
          'Rosa, capim-limão, malagueta',
          'Gerânio, rosa, unha',
          'Rosa, pau-santo',
          'Uma fusão floral e vibrante que envolve o seu refúgio num aroma delicado, criado para iluminar com amor.',
          'Por cada vela vendida, 1€ reverte para o Hospital de Santa Cruz.',
       460,
          true, false
      ),
      (
          gen_random_uuid(),
          'Pack Doce Coração',
          'VEL_VALENTI_01',
          18.5,
          'Edição Especial Dia dos Namorados. O presente perfeito para celebrar a doçura do amor. Combina o conforto guloso do Pistacho Royal com o romantismo místico da vela Coração de Luz.',
          NOW(), 'MIGRATION', NULL, NULL,
          NULL, NULL, NULL,
          'A vela Pistacho Royal, quente e indulgente, e a vela Coração de Luz, doce e envolvente, criadas para perfumar momentos a dois.',
          'Um gesto simples para celebrar o carinho, a partilha e o amor.',
       710,
          true, true
      ),
      (
          gen_random_uuid(),
          'Pack Coração do mar',
          'VEL_VALENTI_02',
          16.5,
          'Edição Especial Dia dos Namorados. Para amores profundos e tranquilos. Este pack une a frescura revigorante da Vela da Foz com a paixão serena da Coração de Luz.',
          NOW(), 'MIGRATION', NULL, NULL,
          NULL, NULL, NULL,
          'Duas velas que se encontram no cuidado e no conforto. O Coração de Luz, um gesto solidário que ilumina com um aroma floral delicado, e a Foz, suave e cremosa, criada para acalmar e acolher o seu refúgio.',
          'Um convite ao amor, à pausa e ao bem-estar — para si ou para oferecer.',
       710,
          true, true
      ),
      (
          gen_random_uuid(),
          'Pinheiro de natal',
          'VEL_PINH-NATAL',
          12,
          'O cheiro autêntico da floresta no inverno. Uma edição festiva que traz o aroma da madeira e das agulhas de pinheiro para dentro de casa.',
          NOW(), 'MIGRATION', '± 35 Horas', 4,
          NULL, NULL, NULL,
          '',
          '',
            0,
          false, false
      );

INSERT INTO product_stock (id, quantity_on_hand, product_id) VALUES
     (gen_random_uuid(),15, (SELECT id FROM products WHERE sku = 'VEL_FOZ')),
     (gen_random_uuid(), 15, (SELECT id FROM products WHERE sku = 'VEL_PISTACHO')),
     (gen_random_uuid(),15 , (SELECT id FROM products WHERE sku = 'VEL_AMBROSIA')),
     (gen_random_uuid(),2 , (SELECT id FROM products WHERE sku = 'VEL_CORACAO')),
     (gen_random_uuid(),0 , (SELECT id FROM products WHERE sku = 'VEL_PINH-NATAL')),
     (gen_random_uuid(), 20, (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
     (gen_random_uuid(), 20, (SELECT id FROM products WHERE sku = 'VEL_VALENTI_02'));

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
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),2,
    (SELECT id FROM materials WHERE sku = 'COLA-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'TPDO-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'FRAS-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),160,
    (SELECT id FROM materials WHERE sku = 'FTCT-BRX'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),20,
    (SELECT id FROM materials WHERE sku = 'PETAL-VERM'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'CX20-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01')),
(gen_random_uuid(),1,
    (SELECT id FROM materials WHERE sku = 'TACA-001'),
    (SELECT id FROM products WHERE sku = 'VEL_VALENTI_01'));

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
