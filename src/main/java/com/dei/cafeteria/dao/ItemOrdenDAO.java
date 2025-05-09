package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.Producto;
import com.dei.cafeteria.modelo.TamañoProducto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemOrdenDAO extends AbstractDAO<ItemOrden, Integer> {

    private static final String INSERT = "INSERT INTO item_orden (orden_id, producto_id, tamaño_id, cantidad, precio_unitario, precio_con_iva, subtotal, iva, total, notas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE item_orden SET orden_id=?, producto_id=?, tamaño_id=?, cantidad=?, precio_unitario=?, precio_con_iva=?, subtotal=?, iva=?, total=?, notas=? WHERE id=?";
    private static final String DELETE = "DELETE FROM item_orden WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM item_orden WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM item_orden";

    @Override
    public ItemOrden guardar(ItemOrden item) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT,
                    item.getOrden().getId(),
                    item.getProducto().getId(),
                    item.getTamaño() != null ? item.getTamaño().getId() : null,
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getPrecioConIva(),
                    item.getSubtotal(),
                    item.getIva(),
                    item.getTotal(),
                    item.getNotas()
            );
            item.setId(id);
            return item;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar item: " + e.getMessage(), e);
        }
    }

    @Override
    public ItemOrden actualizar(ItemOrden item) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE,
                    item.getOrden().getId(),
                    item.getProducto().getId(),
                    item.getTamaño() != null ? item.getTamaño().getId() : null,
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getPrecioConIva(),
                    item.getSubtotal(),
                    item.getIva(),
                    item.getTotal(),
                    item.getNotas(),
                    item.getId()
            );
            if (filas == 0) throw new DAOException("Item no encontrado con ID: " + item.getId());
            return item;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar item: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Item no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar item: " + e.getMessage(), e);
        }
    }

    @Override
    public ItemOrden buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar item por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ItemOrden> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar items: " + e.getMessage(), e);
        }
    }

    private ItemOrden mapear(ResultSet rs) throws SQLException {
        Orden orden = new Orden();
        orden.setId(rs.getInt("orden_id"));

        Producto producto = new Producto();
        producto.setId(rs.getInt("producto_id"));

        TamañoProducto tamaño = new TamañoProducto();
        tamaño.setId(rs.getInt("tamaño_id"));

        return ItemOrden.builder()
                .id(rs.getInt("id"))
                .orden(orden)
                .producto(producto)
                .tamaño(tamaño)
                .cantidad(rs.getDouble("cantidad"))
                .precioUnitario(rs.getDouble("precio_unitario"))
                .precioConIva(rs.getDouble("precio_con_iva"))
                .subtotal(rs.getDouble("subtotal"))
                .iva(rs.getDouble("iva"))
                .total(rs.getDouble("total"))
                .notas(rs.getString("notas"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}