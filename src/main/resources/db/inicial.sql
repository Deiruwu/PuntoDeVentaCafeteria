-- Script SQL para Sistema de Punto de Venta de Cafetería
-- Base de datos: SQLite

PRAGMA foreign_keys = ON;

-- Tabla para Empleados
CREATE TABLE IF NOT EXISTS empleado (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        nombre TEXT NOT NULL,
                                        rol TEXT NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'MESERO', 'CAJERO')),
                                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Usuarios (autenticación)
CREATE TABLE IF NOT EXISTS usuario (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       empleado_id INTEGER NOT NULL,
                                       username TEXT NOT NULL UNIQUE,
                                       password_hash TEXT NOT NULL,
                                       salt TEXT NOT NULL,
                                       ultimo_login TIMESTAMP,
                                       activo BOOLEAN DEFAULT TRUE,
                                       fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (empleado_id) REFERENCES empleado(id) ON DELETE CASCADE
);

-- Índice para búsqueda rápida de usuarios por username
CREATE INDEX idx_usuario_username ON usuario(username);

-- Tabla para Mesas
CREATE TABLE IF NOT EXISTS mesa (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    numero INTEGER NOT NULL UNIQUE,
                                    estado TEXT DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'RESERVADA')),
                                    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Categorías de Productos
CREATE TABLE IF NOT EXISTS categoria_producto (
                                                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                  nombre TEXT NOT NULL UNIQUE,
                                                  descripcion TEXT,
                                                  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para Productos
CREATE TABLE IF NOT EXISTS producto (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        nombre TEXT NOT NULL,
                                        precio REAL NOT NULL CHECK (precio >= 0),
                                        descripcion TEXT,
                                        categoria_id INTEGER,
                                        disponible BOOLEAN DEFAULT TRUE,
                                        imagen_url TEXT,
                                        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (categoria_id) REFERENCES categoria_producto(id) ON DELETE SET NULL
);

-- Índice para búsqueda rápida de productos por nombre
CREATE INDEX idx_producto_nombre ON producto(nombre);
-- Índice para filtrado de productos por categoría
CREATE INDEX idx_producto_categoria ON producto(categoria_id);

-- Tabla para Órdenes
CREATE TABLE IF NOT EXISTS orden (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     mesa_id INTEGER NOT NULL,
                                     mesero_id INTEGER NOT NULL,
                                     estado TEXT DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTA', 'ENTREGADA', 'PAGADA', 'CANCELADA')),
                                     total REAL DEFAULT 0 CHECK (total >= 0),
                                     notas TEXT,
                                     fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (mesa_id) REFERENCES mesa(id) ON DELETE RESTRICT,
                                     FOREIGN KEY (mesero_id) REFERENCES empleado(id) ON DELETE RESTRICT
);

-- Índices para consultas comunes en órdenes
CREATE INDEX idx_orden_mesa ON orden(mesa_id);
CREATE INDEX idx_orden_mesero ON orden(mesero_id);
CREATE INDEX idx_orden_estado ON orden(estado);
CREATE INDEX idx_orden_fecha ON orden(fecha_hora);

-- Tabla para Items de Orden
CREATE TABLE IF NOT EXISTS item_orden (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          orden_id INTEGER NOT NULL,
                                          producto_id INTEGER NOT NULL,
                                          cantidad INTEGER NOT NULL CHECK (cantidad > 0),
                                          precio_unitario REAL NOT NULL CHECK (precio_unitario >= 0),
                                          subtotal REAL NOT NULL CHECK (subtotal >= 0),
                                          notas TEXT,
                                          fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          FOREIGN KEY (orden_id) REFERENCES orden(id) ON DELETE CASCADE,
                                          FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT
);

-- Índices para consultas comunes en ítems
CREATE INDEX idx_item_orden ON item_orden(orden_id);
CREATE INDEX idx_item_producto ON item_orden(producto_id);

-- Tabla para Pagos
CREATE TABLE IF NOT EXISTS pago (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    orden_id INTEGER NOT NULL,
                                    cajero_id INTEGER NOT NULL,
                                    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    monto REAL NOT NULL CHECK (monto > 0),
                                    metodo_pago TEXT NOT NULL CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'OTRO')),
                                    referencia TEXT,
                                    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (orden_id) REFERENCES orden(id) ON DELETE RESTRICT,
                                    FOREIGN KEY (cajero_id) REFERENCES empleado(id) ON DELETE RESTRICT
);

-- Índices para consultas en pagos
CREATE INDEX idx_pago_orden ON pago(orden_id);
CREATE INDEX idx_pago_cajero ON pago(cajero_id);
CREATE INDEX idx_pago_fecha ON pago(fecha_hora);

-- Trigger para actualizar fecha_actualizacion en empleado
CREATE TRIGGER update_empleado_fecha_actualizacion
    AFTER UPDATE ON empleado
    FOR EACH ROW
BEGIN
    UPDATE empleado SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en usuario
CREATE TRIGGER update_usuario_fecha_actualizacion
    AFTER UPDATE ON usuario
    FOR EACH ROW
BEGIN
    UPDATE usuario SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en mesa
CREATE TRIGGER update_mesa_fecha_actualizacion
    AFTER UPDATE ON mesa
    FOR EACH ROW
BEGIN
    UPDATE mesa SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en producto
CREATE TRIGGER update_producto_fecha_actualizacion
    AFTER UPDATE ON producto
    FOR EACH ROW
BEGIN
    UPDATE producto SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en categoria_producto
CREATE TRIGGER update_categoria_producto_fecha_actualizacion
    AFTER UPDATE ON categoria_producto
    FOR EACH ROW
BEGIN
    UPDATE categoria_producto SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en orden
CREATE TRIGGER update_orden_fecha_actualizacion
    AFTER UPDATE ON orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en item_orden
CREATE TRIGGER update_item_orden_fecha_actualizacion
    AFTER UPDATE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE item_orden SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar fecha_actualizacion en pago
CREATE TRIGGER update_pago_fecha_actualizacion
    AFTER UPDATE ON pago
    FOR EACH ROW
BEGIN
    UPDATE pago SET fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = OLD.id;
END;

-- Trigger para actualizar el total de la orden cuando se añade, actualiza o elimina un item
CREATE TRIGGER update_orden_total_insert
    AFTER INSERT ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET total = (SELECT SUM(subtotal) FROM item_orden WHERE orden_id = NEW.orden_id)
    WHERE id = NEW.orden_id;
END;

CREATE TRIGGER update_orden_total_update
    AFTER UPDATE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET total = (SELECT SUM(subtotal) FROM item_orden WHERE orden_id = NEW.orden_id)
    WHERE id = NEW.orden_id;
END;

CREATE TRIGGER update_orden_total_delete
    AFTER DELETE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE orden SET total = (SELECT SUM(subtotal) FROM item_orden WHERE orden_id = OLD.orden_id)
    WHERE id = OLD.orden_id;
END;

-- Trigger para asegurar que el subtotal = precio_unitario * cantidad
CREATE TRIGGER calculate_subtotal_insert
    BEFORE INSERT ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE item_orden SET subtotal = NEW.precio_unitario * NEW.cantidad WHERE id = NEW.id;
END;

CREATE TRIGGER calculate_subtotal_update
    BEFORE UPDATE ON item_orden
    FOR EACH ROW
BEGIN
    UPDATE item_orden SET subtotal = NEW.precio_unitario * NEW.cantidad WHERE id = NEW.id;
END;

-- Trigger para actualizar el estado de la mesa cuando se crea o actualiza una orden
CREATE TRIGGER update_mesa_estado_orden_insert
    AFTER INSERT ON orden
    FOR EACH ROW
BEGIN
    UPDATE mesa SET estado = 'OCUPADA' WHERE id = NEW.mesa_id AND estado != 'OCUPADA';
END;

CREATE TRIGGER update_mesa_estado_orden_update
    AFTER UPDATE ON orden
    FOR EACH ROW
BEGIN
    UPDATE mesa SET estado =
                        CASE
                            WHEN NEW.estado IN ('PAGADA', 'CANCELADA') THEN 'DISPONIBLE'
                            ELSE 'OCUPADA'
                            END
    WHERE id = NEW.mesa_id;
END;