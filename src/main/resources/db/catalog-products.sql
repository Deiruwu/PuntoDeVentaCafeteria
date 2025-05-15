-- BEBIDAS FRÍAS
INSERT INTO producto (nombre, precio_base, aplica_iva, descripcion, categoria_id, stock_actual, stock_minimo, imagen_url) VALUES
    -- Cafés Fríos
    ('Frappuccino', 55.00, 1, 'Café mezclado con hielo y crema batida', 2, 60, 8, '/imagenes/productos/frappuccino.png'),
    ('Mocha Frío', 58.00, 1, 'Café espresso con chocolate y leche fría', 2, 70, 8, '/imagenes/productos/mocha_frio.png'),
    ('Cappuccino Helado', 52.00, 1, 'Capuccino con hielo y espuma de leche fría', 2, 65, 8, '/imagenes/productos/cappuccino_helado.png'),
    
    -- Chocolates y otras bebidas frías
    ('Chocolate Frío', 50.00, 1, 'Chocolate con leche fría y hielo', 2, 60, 8, '/imagenes/productos/chocolate_frio.png'),
    ('Matcha Frío', 58.00, 1, 'Té matcha con leche fría y hielo', 2, 50, 6, '/imagenes/productos/matcha_frio.png'),
    
    -- Smoothies y Batidos
    ('Smoothie de Fresa', 60.00, 1, 'Batido cremoso de fresas frescas', 2, 40, 5, '/imagenes/productos/smoothie_fresa.png'),
    ('Batido de Chocolate', 62.00, 1, 'Batido cremoso de chocolate con helado', 2, 35, 5, '/imagenes/productos/batido_chocolate.png'),
    
    -- Tés Helados
    ('Té Helado de Limón', 42.00, 1, 'Té negro con limón y hielo', 2, 70, 10, '/imagenes/productos/te_helado_limon.png'),
    ('Té Helado de Durazno', 42.00, 1, 'Té negro con sabor a durazno y hielo', 2, 70, 10, '/imagenes/productos/te_helado_durazno.png');

-- POSTRES
INSERT INTO producto (nombre, precio_base, aplica_iva, descripcion, categoria_id, stock_actual, stock_minimo, imagen_url) VALUES
    -- Pasteles
    ('Cheesecake de Limón', 58.00, 1, 'Tarta de queso con toque de limón', 3, 15, 3, '/imagenes/productos/cheesecake_limon.png'),
    ('Pastel Tres Leches', 54.00, 1, 'Bizcocho húmedo bañado en tres tipos de leche', 3, 15, 3, '/imagenes/productos/tres_leches.png');

-- SNACKS
INSERT INTO producto (nombre, precio_base, aplica_iva, descripcion, categoria_id, stock_actual, stock_minimo, imagen_url) VALUES
    -- Brownies y Galletas
    ('Brownie', 38.00, 1, 'Brownie denso de chocolate con nueces', 5, 30, 6, '/imagenes/productos/brownie.png'),
    ('Brownie con Helado', 55.00, 1, 'Brownie caliente con bola de helado de vainilla', 5, 25, 5, '/imagenes/productos/brownie_helado.png'),
    ('Galleta de Chispas de Chocolate', 30.00, 1, 'Galleta suave con abundantes chispas de chocolate', 5, 40, 8, '/imagenes/productos/galleta_chispas.png'),
    ('Galleta de Avena y Pasas', 30.00, 1, 'Galleta de avena integral con pasas', 5, 40, 8, '/imagenes/productos/galleta_avena.png'),
    ('Galleta de Mantequilla', 28.00, 1, 'Galleta tradicional de mantequilla', 5, 40, 8, '/imagenes/productos/galleta_mantequilla.png');

-- PLATOS PRINCIPALES ADICIONALES
INSERT INTO producto (nombre, precio_base, aplica_iva, descripcion, categoria_id, stock_actual, stock_minimo, imagen_url) VALUES
    ('Panini de Pavo', 65.00, 1, 'Sándwich caliente con pavo, queso y pesto', 4, 20, 4, '/imagenes/productos/panini_pavo.png'),
    ('Panini Caprese', 60.00, 1, 'Sándwich caliente con tomate, mozzarella y albahaca', 4, 20, 4, '/imagenes/productos/panini_caprese.png'),
    ('Ensalada César', 70.00, 1, 'Ensalada fresca con lechuga, pollo, crutones y aderezo césar', 4, 15, 3, '/imagenes/productos/ensalada_cesar.png');

-- BEBIDAS CALIENTES COMPLEMENTARIAS
INSERT INTO producto (nombre, precio_base, aplica_iva, descripcion, categoria_id, stock_actual, stock_minimo, imagen_url) VALUES
    ('Espresso', 35.00, 1, 'Shot concentrado de café', 1, 100, 10, '/imagenes/productos/espresso.png'),
    ('Latte', 48.00, 1, 'Café espresso con leche caliente', 1, 90, 10, '/imagenes/productos/latte.png'),
    ('Chocolate Caliente', 48.00, 1, 'Bebida cremosa de chocolate', 1, 80, 10, '/imagenes/productos/chocolate_caliente.png'),
    ('Té Verde', 40.00, 1, 'Té verde tradicional japonés', 1, 70, 8, '/imagenes/productos/te_verde.png'),
    ('Té de Manzanilla', 38.00, 1, 'Infusión relajante de manzanilla', 1, 60, 8, '/imagenes/productos/te_manzanilla.png');

-- Asignación de tamaños a productos nuevos
-- Para los cafés fríos
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) 
SELECT p.id, t.id, 1
FROM producto p, tamaño_producto t
WHERE p.nombre IN ('Frappuccino', 'Mocha Frío', 'Cappuccino Helado', 'Chocolate Frío', 'Matcha Frío')
  AND t.nombre IN ('Chico', 'Mediano', 'Grande');

-- Para los smoothies y batidos
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) 
SELECT p.id, t.id, 1
FROM producto p, tamaño_producto t
WHERE p.nombre IN ('Smoothie de Fresa', 'Batido de Chocolate')
  AND t.nombre IN ('Chico', 'Mediano', 'Grande');

-- Para tés helados
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) 
SELECT p.id, t.id, 1
FROM producto p, tamaño_producto t
WHERE p.nombre IN ('Té Helado de Limón', 'Té Helado de Durazno')
  AND t.nombre IN ('Chico', 'Mediano', 'Grande');

-- Para los pasteles (rebanada y completo)
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) 
SELECT p.id, t.id, 1
FROM producto p, tamaño_producto t
WHERE p.nombre IN ('Cheesecake de Limón', 'Pastel Tres Leches')
  AND t.nombre IN ('Rebanada', 'Completo');

-- Para bebidas calientes complementarias
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) 
SELECT p.id, t.id, 1
FROM producto p, tamaño_producto t
WHERE p.nombre IN ('Espresso', 'Latte', 'Chocolate Caliente', 'Té Verde', 'Té de Manzanilla')
  AND t.nombre IN ('Chico', 'Mediano', 'Grande');

-- Para snacks y otros productos que solo tienen un tamaño
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) 
SELECT p.id, t.id, 1
FROM producto p, tamaño_producto t
WHERE p.nombre IN ('Brownie', 'Brownie con Helado', 'Galleta de Chispas de Chocolate', 'Galleta de Avena y Pasas', 'Galleta de Mantequilla',
                  'Panini de Pavo', 'Panini Caprese', 'Ensalada César')
  AND t.nombre = 'Chico';