-- Script SQL para Sistema de Punto de Venta de Cafetería (Versión Simplificada)
-- Base de datos: SQLite

PRAGMA foreign_keys = ON;

-- Tabla para Roles de Empleados
CREATE TABLE IF NOT EXISTS rol (
                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                   nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('ADMINISTRADOR', 'MESERO', 'CAJERO', 'COCINERO')),
                                   descripcion TEXT,
                                   fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Empleados (simplificada)
CREATE TABLE IF NOT EXISTS empleado (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        nombre TEXT NOT NULL,
                                        apellido TEXT NOT NULL,
                                        rol_id INTEGER NOT NULL,
                                        imagen_url TEXT DEFAULT '/imagenes/empleados/default.png',
                                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE RESTRICT
);

-- Tabla para Estados de Usuario
CREATE TABLE IF NOT EXISTS estado_usuario (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('ACTIVO', 'INACTIVO')),
                                              descripcion TEXT,
                                              fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Usuarios (autenticación)
CREATE TABLE IF NOT EXISTS usuario (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       empleado_id INTEGER NOT NULL,
                                       nombre_usuario TEXT NOT NULL UNIQUE,
                                       hash_contraseña TEXT NOT NULL, -- Ya incluye el salt en el hash (bcrypt)
                                       ultimo_login TIMESTAMP,
                                       estado_id INTEGER NOT NULL DEFAULT 1, -- 1=ACTIVO
                                       fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (empleado_id) REFERENCES empleado(id) ON DELETE CASCADE,
                                       FOREIGN KEY (estado_id) REFERENCES estado_usuario(id) ON DELETE RESTRICT
);

-- Índice para búsqueda rápida de usuarios por nombre_usuario
CREATE INDEX idx_usuario_nombre_usuario ON usuario(nombre_usuario);

-- Tabla para Estados de Mesa (simplificada)
CREATE TABLE IF NOT EXISTS estado_mesa (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('DISPONIBLE', 'OCUPADA', 'RESERVADA')),
                                           descripcion TEXT,
                                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Mesas
CREATE TABLE IF NOT EXISTS mesa (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    numero INTEGER NOT NULL UNIQUE,
                                    capacidad INTEGER NOT NULL DEFAULT 4 CHECK (capacidad > 0),
                                    estado_id INTEGER NOT NULL DEFAULT 1, -- 1=DISPONIBLE
                                    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (estado_id) REFERENCES estado_mesa(id) ON DELETE RESTRICT
);

-- Tabla para Categorías de Productos
CREATE TABLE IF NOT EXISTS categoria_producto (
                                                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                  nombre TEXT NOT NULL UNIQUE,
                                                  descripcion TEXT,
                                                  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Tamaños de Producto
CREATE TABLE IF NOT EXISTS tamaño_producto (
                                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                                               nombre TEXT NOT NULL UNIQUE,
                                               factor_precio REAL NOT NULL DEFAULT 1.0, -- Factor multiplicador para el precio base
                                               es_porcion BOOLEAN DEFAULT FALSE, -- TRUE si es porción (rebanada), FALSE si no
                                               fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Productos
CREATE TABLE IF NOT EXISTS producto (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        nombre TEXT NOT NULL,
                                        precio_base REAL NOT NULL CHECK (precio_base >= 0), -- Precio base sin IVA
                                        aplica_iva BOOLEAN DEFAULT TRUE, -- Si se aplica IVA (16%)
                                        descripcion TEXT,
                                        categoria_id INTEGER,
                                        disponible BOOLEAN DEFAULT TRUE,
                                        stock_actual REAL DEFAULT 0 CHECK (stock_actual >= 0),
                                        stock_minimo REAL DEFAULT 5 CHECK (stock_minimo >= 0),
                                        imagen_url TEXT DEFAULT '/imagenes/productos/default.png',
                                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (categoria_id) REFERENCES categoria_producto(id) ON DELETE SET NULL
);

-- Tabla de relación Producto-Tamaño (permite asignar tamaños a productos)
CREATE TABLE IF NOT EXISTS producto_tamaño (
                                               producto_id INTEGER NOT NULL,
                                               tamaño_id INTEGER NOT NULL,
                                               activo BOOLEAN DEFAULT TRUE,
                                               fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               PRIMARY KEY (producto_id, tamaño_id),
                                               FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
                                               FOREIGN KEY (tamaño_id) REFERENCES tamaño_producto(id) ON DELETE CASCADE
);

-- Índice para búsqueda rápida de productos por nombre
CREATE INDEX idx_producto_nombre ON producto(nombre);
-- Índice para filtrado de productos por categoría
CREATE INDEX idx_producto_categoria ON producto(categoria_id);
-- Índice para alertas de stock bajo
CREATE INDEX idx_producto_stock ON producto(stock_actual);

-- Tabla para Tipos de Movimiento (simplificada)
CREATE TABLE IF NOT EXISTS tipo_movimiento (
                                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                                               nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('ENTRADA', 'SALIDA')),
                                               descripcion TEXT,
                                               fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Movimientos de Inventario
CREATE TABLE IF NOT EXISTS movimiento_inventario (
                                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                     producto_id INTEGER NOT NULL,
                                                     tipo_movimiento_id INTEGER NOT NULL,
                                                     cantidad REAL NOT NULL,
                                                     stock_previo REAL NOT NULL CHECK (stock_previo >= 0),
                                                     stock_nuevo REAL NOT NULL CHECK (stock_nuevo >= 0),
                                                     empleado_id INTEGER NOT NULL,
                                                     referencia TEXT, -- Referencia a orden, factura, etc.
                                                     fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                     fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                     FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT,
                                                     FOREIGN KEY (tipo_movimiento_id) REFERENCES tipo_movimiento(id) ON DELETE RESTRICT,
                                                     FOREIGN KEY (empleado_id) REFERENCES empleado(id) ON DELETE RESTRICT
);

-- Índices para consultas comunes en movimientos de inventario
CREATE INDEX idx_movimiento_producto ON movimiento_inventario(producto_id);
CREATE INDEX idx_movimiento_tipo ON movimiento_inventario(tipo_movimiento_id);
CREATE INDEX idx_movimiento_fecha ON movimiento_inventario(fecha_creacion);

-- Tabla para Estados de Orden
CREATE TABLE IF NOT EXISTS estado_orden (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('PENDIENTE', 'EN_PREPARACION', 'LISTA', 'ENTREGADA', 'PAGADA', 'CANCELADA')),
                                            descripcion TEXT,
                                            fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Órdenes
CREATE TABLE IF NOT EXISTS orden (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     mesa_id INTEGER NOT NULL,
                                     mesero_id INTEGER NOT NULL,
                                     estado_id INTEGER NOT NULL DEFAULT 1, -- 1=PENDIENTE
                                     subtotal REAL DEFAULT 0 CHECK (subtotal >= 0), -- Subtotal sin IVA
                                     iva REAL DEFAULT 0 CHECK (iva >= 0), -- Monto de IVA
                                     total REAL DEFAULT 0 CHECK (total >= 0), -- Total con IVA
                                     notas TEXT,
                                     fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (mesa_id) REFERENCES mesa(id) ON DELETE RESTRICT,
                                     FOREIGN KEY (mesero_id) REFERENCES empleado(id) ON DELETE RESTRICT,
                                     FOREIGN KEY (estado_id) REFERENCES estado_orden(id) ON DELETE RESTRICT
);

-- Índices para consultas comunes en órdenes
CREATE INDEX idx_orden_mesa ON orden(mesa_id);
CREATE INDEX idx_orden_mesero ON orden(mesero_id);
CREATE INDEX idx_orden_estado ON orden(estado_id);
CREATE INDEX idx_orden_fecha ON orden(fecha_hora);

-- Tabla para Items de Orden
CREATE TABLE IF NOT EXISTS item_orden (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          orden_id INTEGER NOT NULL,
                                          producto_id INTEGER NOT NULL,
                                          tamaño_id INTEGER NOT NULL,
                                          cantidad REAL NOT NULL CHECK (cantidad > 0),
                                          precio_unitario REAL NOT NULL CHECK (precio_unitario >= 0), -- Precio con factor tamaño aplicado
                                          precio_con_iva REAL NOT NULL CHECK (precio_con_iva >= 0), -- Precio unitario con IVA incluido
                                          subtotal REAL NOT NULL CHECK (subtotal >= 0), -- Subtotal sin IVA
                                          iva REAL DEFAULT 0 CHECK (iva >= 0), -- IVA del ítem
                                          total REAL NOT NULL CHECK (total >= 0), -- Total con IVA
                                          notas TEXT,
                                          fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (orden_id) REFERENCES orden(id) ON DELETE CASCADE,
                                          FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT,
                                          FOREIGN KEY (tamaño_id) REFERENCES tamaño_producto(id) ON DELETE RESTRICT
);

-- Índices para consultas comunes en ítems
CREATE INDEX idx_item_orden ON item_orden(orden_id);
CREATE INDEX idx_item_producto ON item_orden(producto_id);

-- Tabla para Métodos de Pago (simplificada)
CREATE TABLE IF NOT EXISTS metodo_pago (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           nombre TEXT NOT NULL UNIQUE CHECK (nombre IN ('EFECTIVO', 'TARJETA')),
                                           descripcion TEXT,
                                           requiere_referencia BOOLEAN DEFAULT FALSE,
                                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Pagos
CREATE TABLE IF NOT EXISTS pago (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    orden_id INTEGER NOT NULL,
                                    cajero_id INTEGER NOT NULL,
                                    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    monto REAL NOT NULL CHECK (monto > 0),
                                    metodo_pago_id INTEGER NOT NULL,
                                    referencia TEXT,
                                    cambio REAL DEFAULT 0 CHECK (cambio >= 0),
                                    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (orden_id) REFERENCES orden(id) ON DELETE RESTRICT,
                                    FOREIGN KEY (cajero_id) REFERENCES empleado(id) ON DELETE RESTRICT,
                                    FOREIGN KEY (metodo_pago_id) REFERENCES metodo_pago(id) ON DELETE RESTRICT
);

-- Índices para consultas en pagos
CREATE INDEX idx_pago_orden ON pago(orden_id);
CREATE INDEX idx_pago_cajero ON pago(cajero_id);
CREATE INDEX idx_pago_fecha ON pago(fecha_hora);
CREATE INDEX idx_pago_metodo ON pago(metodo_pago_id);

-- ================================================================
-- TRIGGERS
-- ================================================================

CREATE TRIGGER actualizar_ultimo_login
    AFTER UPDATE ON usuario
    FOR EACH ROW
BEGIN
    UPDATE usuario
    SET ultimo_login = datetime(NEW.ultimo_login, 'unixepoch')
    WHERE id = NEW.id;
END;


-- Trigger para actualizar fecha_actualizacion en todas las tablas
CREATE TRIGGER IF NOT EXISTS update_rol_fecha_actualizacion
    AFTER UPDATE ON rol
    FOR EACH ROW
BEGIN
    UPDATE rol SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Triggers para otras tablas (ejemplo para empleado, repite para todas las tablas)
CREATE TRIGGER IF NOT EXISTS update_empleado_fecha_actualizacion
    AFTER UPDATE ON empleado
    FOR EACH ROW
BEGIN
    UPDATE empleado SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_estado_usuario_fecha_actualizacion
    AFTER UPDATE ON estado_usuario
    FOR EACH ROW
BEGIN
    UPDATE estado_usuario SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_usuario_fecha_actualizacion
    AFTER UPDATE ON usuario
    FOR EACH ROW
BEGIN
    UPDATE usuario SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_estado_mesa_fecha_actualizacion
    AFTER UPDATE ON estado_mesa
    FOR EACH ROW
BEGIN
    UPDATE estado_mesa SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_mesa_fecha_actualizacion
    AFTER UPDATE ON mesa
    FOR EACH ROW
BEGIN
    UPDATE mesa SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_categoria_producto_fecha_actualizacion
    AFTER UPDATE ON categoria_producto
    FOR EACH ROW
BEGIN
    UPDATE categoria_producto SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_tamaño_producto_fecha_actualizacion
    AFTER UPDATE ON tamaño_producto
    FOR EACH ROW
BEGIN
    UPDATE tamaño_producto SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_producto_fecha_actualizacion
    AFTER UPDATE ON producto
    FOR EACH ROW
BEGIN
    UPDATE producto SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_producto_tamaño_fecha_actualizacion
    AFTER UPDATE ON producto_tamaño
    FOR EACH ROW
BEGIN
    UPDATE producto_tamaño SET fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE producto_id = OLD.producto_id AND tamaño_id = OLD.tamaño_id;
END;

CREATE TRIGGER IF NOT EXISTS update_tipo_movimiento_fecha_actualizacion
    AFTER UPDATE ON tipo_movimiento
    FOR EACH ROW
BEGIN
    UPDATE tipo_movimiento SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_movimiento_inventario_fecha_actualizacion
    AFTER UPDATE ON movimiento_inventario
    FOR EACH ROW
BEGIN
    UPDATE movimiento_inventario SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_estado_orden_fecha_actualizacion
    AFTER UPDATE ON estado_orden
    FOR EACH ROW
BEGIN
    UPDATE estado_orden SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_orden_fecha_actualizacion
    AFTER UPDATE ON orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_item_orden_fecha_actualizacion
    AFTER UPDATE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE item_orden SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_metodo_pago_fecha_actualizacion
    AFTER UPDATE ON metodo_pago
    FOR EACH ROW
BEGIN
    UPDATE metodo_pago SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

CREATE TRIGGER IF NOT EXISTS update_pago_fecha_actualizacion
    AFTER UPDATE ON pago
    FOR EACH ROW
BEGIN
    UPDATE pago SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para calcular precio con IVA, subtotal y total en item_orden al insertar
CREATE TRIGGER IF NOT EXISTS calculate_item_valores_insert
    BEFORE INSERT ON item_orden
BEGIN
    -- Obtener si el producto aplica IVA y calculamos los valores necesarios
    INSERT INTO item_orden (
        orden_id,
        producto_id,
        tamaño_id,
        cantidad,
        precio_unitario,
        precio_con_iva,
        subtotal,
        iva,
        total,
        notas,
        fecha_creacion,
        fecha_actualizacion
    )
    SELECT
        NEW.orden_id,
        NEW.producto_id,
        NEW.tamaño_id,
        NEW.cantidad,
        p.precio_base * t.factor_precio, -- precio_unitario con factor aplicado
        CASE WHEN p.aplica_iva THEN (p.precio_base * t.factor_precio) * 1.16 ELSE (p.precio_base * t.factor_precio) END, -- precio_con_iva
        (p.precio_base * t.factor_precio) * NEW.cantidad, -- subtotal
        CASE WHEN p.aplica_iva THEN ((p.precio_base * t.factor_precio) * NEW.cantidad) * 0.16 ELSE 0 END, -- iva
        CASE WHEN p.aplica_iva THEN ((p.precio_base * t.factor_precio) * NEW.cantidad) * 1.16 ELSE (p.precio_base * t.factor_precio) * NEW.cantidad END, -- total
        NEW.notas,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    FROM producto p
             JOIN tamaño_producto t ON t.id = NEW.tamaño_id
    WHERE p.id = NEW.producto_id;
END;

-- Trigger para calcular precio con IVA, subtotal y total en item_orden al actualizar
CREATE TRIGGER IF NOT EXISTS calculate_item_valores_update
    BEFORE INSERT ON item_orden
BEGIN
    -- Si no se están actualizando valores relevantes, sólo actualizamos lo que viene
    UPDATE item_orden SET
                          orden_id = NEW.orden_id,
                          producto_id = NEW.producto_id,
                          tamaño_id = NEW.tamaño_id,
                          cantidad = NEW.cantidad,
                          notas = NEW.notas,
                          fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = OLD.id;

    -- Luego recalculamos los valores monetarios
    UPDATE item_orden SET
                          precio_unitario = (SELECT p.precio_base * t.factor_precio
                                             FROM producto p
                                                      JOIN tamaño_producto t ON t.id = item_orden.tamaño_id
                                             WHERE p.id = item_orden.producto_id),
                          subtotal = (SELECT (p.precio_base * t.factor_precio) * item_orden.cantidad
                                      FROM producto p
                                               JOIN tamaño_producto t ON t.id = item_orden.tamaño_id
                                      WHERE p.id = item_orden.producto_id)
    WHERE id = OLD.id;

    -- Finalmente calculamos IVA y totales
    UPDATE item_orden SET
                          iva = CASE
                                    WHEN (SELECT p.aplica_iva FROM producto p WHERE p.id = item_orden.producto_id)
                                        THEN subtotal * 0.16
                                    ELSE 0
                              END,
                          precio_con_iva = CASE
                                               WHEN (SELECT p.aplica_iva FROM producto p WHERE p.id = item_orden.producto_id)
                                                   THEN precio_unitario * 1.16
                                               ELSE precio_unitario
                              END,
                          total = subtotal + iva
    WHERE id = OLD.id;
END;

-- Trigger para actualizar el total de la orden cuando se añade un item
CREATE TRIGGER IF NOT EXISTS update_orden_total_insert
    AFTER INSERT ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET
                     subtotal = (SELECT COALESCE(SUM(subtotal), 0) FROM item_orden WHERE orden_id = NEW.orden_id),
                     iva = (SELECT COALESCE(SUM(iva), 0) FROM item_orden WHERE orden_id = NEW.orden_id),
                     total = (SELECT COALESCE(SUM(total), 0) FROM item_orden WHERE orden_id = NEW.orden_id),
                     fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = NEW.orden_id;
END;

-- Trigger para actualizar el total de la orden cuando se actualiza un item
CREATE TRIGGER IF NOT EXISTS update_orden_total_update
    AFTER UPDATE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET
                     subtotal = (SELECT COALESCE(SUM(subtotal), 0) FROM item_orden WHERE orden_id = NEW.orden_id),
                     iva = (SELECT COALESCE(SUM(iva), 0) FROM item_orden WHERE orden_id = NEW.orden_id),
                     total = (SELECT COALESCE(SUM(total), 0) FROM item_orden WHERE orden_id = NEW.orden_id),
                     fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = NEW.orden_id;
END;

-- Trigger para actualizar el total de la orden cuando se elimina un item
CREATE TRIGGER IF NOT EXISTS update_orden_total_delete
    AFTER DELETE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET
                     subtotal = (SELECT COALESCE(SUM(subtotal), 0) FROM item_orden WHERE orden_id = OLD.orden_id),
                     iva = (SELECT COALESCE(SUM(iva), 0) FROM item_orden WHERE orden_id = OLD.orden_id),
                     total = (SELECT COALESCE(SUM(total), 0) FROM item_orden WHERE orden_id = OLD.orden_id),
                     fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = OLD.orden_id;
END;

-- Trigger para actualizar el estado de la mesa cuando se crea una orden
CREATE TRIGGER IF NOT EXISTS update_mesa_estado_orden_insert
    AFTER INSERT ON orden
    FOR EACH ROW
BEGIN
    UPDATE mesa SET
                    estado_id = 2, -- 2=OCUPADA
                    fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = NEW.mesa_id AND estado_id = 1; -- 1=DISPONIBLE
END;

-- Trigger para actualizar el estado de la mesa cuando se actualiza el estado de una orden
CREATE TRIGGER IF NOT EXISTS update_mesa_estado_orden_update
    AFTER UPDATE OF estado_id ON orden
    FOR EACH ROW
BEGIN
    UPDATE mesa SET
                    estado_id = CASE
                                    WHEN NEW.estado_id IN (5, 6) THEN 1 -- 5=PAGADA, 6=CANCELADA, 1=DISPONIBLE
                                    ELSE 2 -- 2=OCUPADA
                        END,
                    fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = NEW.mesa_id;
END;

-- Trigger para actualizar stock cuando se crea un item de orden
CREATE TRIGGER IF NOT EXISTS update_stock_item_orden_insert
    AFTER INSERT ON item_orden
    FOR EACH ROW
BEGIN
    -- Guardar stock previo para el movimiento de inventario
    INSERT INTO movimiento_inventario (
        producto_id,
        tipo_movimiento_id,
        cantidad,
        stock_previo,
        stock_nuevo,
        empleado_id,
        referencia
    )
    SELECT
        NEW.producto_id,
        2, -- 2=SALIDA
        NEW.cantidad,
        p.stock_actual,
        p.stock_actual - NEW.cantidad,
        (SELECT mesero_id FROM orden WHERE id = NEW.orden_id),
        'Orden #' || NEW.orden_id
    FROM producto p
    WHERE p.id = NEW.producto_id;

    -- Actualizar stock_actual en producto
    UPDATE producto
    SET
        stock_actual = stock_actual - NEW.cantidad,
        disponible = CASE WHEN (stock_actual - NEW.cantidad) <= 0 THEN 0 ELSE 1 END,
        fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = NEW.producto_id;
END;

-- Trigger para actualizar stock cuando se elimina un item de orden
CREATE TRIGGER IF NOT EXISTS update_stock_item_orden_delete
    AFTER DELETE ON item_orden
    FOR EACH ROW
BEGIN
    -- Guardar stock previo para el movimiento de inventario
    INSERT INTO movimiento_inventario (
        producto_id,
        tipo_movimiento_id,
        cantidad,
        stock_previo,
        stock_nuevo,
        empleado_id,
        referencia
    )
    SELECT
        OLD.producto_id,
        1, -- 1=ENTRADA
        OLD.cantidad,
        p.stock_actual,
        p.stock_actual + OLD.cantidad,
        (SELECT mesero_id FROM orden WHERE id = OLD.orden_id),
        'Cancelación Item Orden #' || OLD.orden_id
    FROM producto p
    WHERE p.id = OLD.producto_id;

    -- Restaurar stock
    UPDATE producto
    SET
        stock_actual = stock_actual + OLD.cantidad,
        disponible = CASE WHEN (stock_actual + OLD.cantidad) > 0 THEN 1 ELSE 0 END,
        fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = OLD.producto_id;
END;

-- ================================================================
-- DATOS INICIALES
-- ================================================================

-- Insertar roles básicos
INSERT INTO rol (id, nombre, descripcion) VALUES
                                              (1, 'ADMINISTRADOR', 'Acceso completo al sistema'),
                                              (2, 'CAJERO', 'Gestión de pagos y cierre de órdenes'),
                                              (3, 'MESERO', 'Gestión de órdenes y mesas'),
                                              (4, 'COCINERO', 'Preparación de productos');

-- Insertar estados de usuario
INSERT INTO estado_usuario (id, nombre, descripcion) VALUES
                                                         (1, 'ACTIVO', 'Usuario con acceso al sistema'),
                                                         (2, 'INACTIVO', 'Usuario sin acceso temporal');

-- Insertar estados de mesa
INSERT INTO estado_mesa (id, nombre, descripcion) VALUES
                                                      (1, 'DISPONIBLE', 'Mesa lista para recibir clientes'),
                                                      (2, 'OCUPADA', 'Mesa con clientes y orden en curso'),
                                                      (3, 'RESERVADA', 'Mesa reservada para futuro uso');

-- Insertar estados de orden
INSERT INTO estado_orden (id, nombre, descripcion) VALUES
                                                       (1, 'PENDIENTE', 'Orden recién creada'),
                                                       (2, 'EN_PREPARACION', 'Orden en proceso de preparación'),
                                                       (3, 'LISTA', 'Orden lista para entregar'),
                                                       (4, 'ENTREGADA', 'Orden entregada al cliente'),
                                                       (5, 'PAGADA', 'Orden pagada y completada'),
                                                       (6, 'CANCELADA', 'Orden cancelada');

-- Insertar tipos de movimiento de inventario
INSERT INTO tipo_movimiento (id, nombre, descripcion) VALUES
                                                          (1, 'ENTRADA', 'Ingreso de productos al inventario'),
                                                          (2, 'SALIDA', 'Salida de productos por venta');

-- Insertar métodos de pago
INSERT INTO metodo_pago (id, nombre, descripcion, requiere_referencia) VALUES
                                                                           (1, 'EFECTIVO', 'Pago con dinero en efectivo', 0),
                                                                           (2, 'TARJETA', 'Pago con tarjeta bancaria', 1);

-- Insertar tamaños de productos
INSERT INTO tamaño_producto (id, nombre, factor_precio, es_porcion) VALUES
                                                                        (1, 'Chico', 1.0, 0),             -- Tamaño base (factor 1.0)
                                                                        (2, 'Mediano', 1.35, 0),          -- 35% más caro que el chico
                                                                        (3, 'Grande', 1.55, 0),           -- 55% más caro que el chico
                                                                        (4, 'Rebanada', 1.0, 1),          -- Una rebanada (factor 1.0)
                                                                        (5, 'Completo', 6.0, 0);          -- Pastel completo (equivale a 6 rebanadas aprox.)

-- Insertar empleado administrador
INSERT INTO empleado (id, nombre, apellido, rol_id, imagen_url) VALUES
    (1, 'Desidere', 'Selene', 1, '/imagenes/empleados/admin.png');

-- Insertar usuario administrador (contraseña: 29Demayo$)
INSERT INTO usuario (id, empleado_id, nombre_usuario, hash_contraseña, estado_id) VALUES
    (1, 1, 'admin', '$2a$10$.G4t4mSrfTduj30x.8oFZe4lDc2CdsJ6W32U4YdcduLjTX4ElviGW', 1);

-- Insertar algunas mesas por defecto
INSERT INTO mesa (id, numero, capacidad) VALUES
                                             (1, 1, 2),
                                             (2, 2, 2),
                                             (3, 3, 4),
                                             (4, 4, 4),
                                             (5, 5, 6);

-- Insertar categorías de productos básicas
INSERT INTO categoria_producto (id, nombre, descripcion) VALUES
                                                             (1, 'Bebidas calientes', 'Café, té y otras bebidas calientes'),
                                                             (2, 'Bebidas frías', 'Refrescos, jugos y bebidas frías'),
                                                             (3, 'Postres', 'Pasteles, galletas y otros postres'),
                                                             (4, 'Platos principales', 'Comidas y platos principales'),
                                                             (5, 'Snacks', 'Bocadillos y aperitivos');

-- Insertar algunos productos de ejemplo
INSERT INTO producto (id, nombre, precio_base, aplica_iva, descripcion, categoria_id, stock_actual, stock_minimo) VALUES
                                                                                                                      (1, 'Café Americano', 40.00, 1, 'Café negro tradicional', 1, 100, 10),
                                                                                                                      (2, 'Cappuccino', 45.00, 1, 'Café con leche espumosa', 1, 100, 10),
                                                                                                                      (3, 'Limonada', 35.00, 1, 'Limonada fresca natural', 2, 50, 5),
                                                                                                                      (4, 'Pastel de chocolate', 52.00, 1, 'Delicioso pastel de chocolate', 3, 20, 3),
                                                                                                                      (5, 'Sandwich de jamón y queso', 45.00, 1, 'Sandwich en pan artesanal', 4, 15, 3);

-- Asignar tamaños disponibles a productos
INSERT INTO producto_tamaño (producto_id, tamaño_id, activo) VALUES
-- Café Americano disponible en tres tamaños
(1, 1, 1), -- Chico
(1, 2, 1), -- Mediano
(1, 3, 1), -- Grande
-- Cappuccino disponible en tres tamaños
(2, 1, 1), -- Chico
(2, 2, 1), -- Mediano
(2, 3, 1), -- Grande
-- Limonada disponible en tres tamaños
(3, 1, 1), -- Chico
(3, 2, 1), -- Mediano
(3, 3, 1), -- Grande
-- Pastel disponible en rebanada y completo
(4, 4, 1), -- Rebanada
(4, 5, 1), -- Completo
-- Sandwich solo disponible en tamaño único (chico)
(5, 1, 1); -- Chico