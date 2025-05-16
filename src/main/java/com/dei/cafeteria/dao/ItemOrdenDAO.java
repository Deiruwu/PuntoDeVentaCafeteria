package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.Producto;
import com.dei.cafeteria.modelo.TamañoProducto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemOrdenDAO extends AbstractDAO<ItemOrden, Integer> {

    // Campos eliminados: precio_unitario, precio_con_iva, subtotal, iva, total
    private static final String INSERT = "INSERT INTO vista_item_orden (orden_id, producto_id, tamaño_id, cantidad, notas) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE item_orden SET orden_id=?, producto_id=?, tamaño_id=?, cantidad=?, notas=? WHERE id=?";
    private static final String DELETE = "DELETE FROM item_orden WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM item_orden WHERE id=?";
    private static final String FIND_BY_ORDEN_ID = "SELECT * FROM item_orden WHERE orden_id=?";
    private static final String FIND_ALL = "SELECT * FROM item_orden";

    @Override
    public ItemOrden guardar(ItemOrden item) throws DAOException {
        try {
            int id = ejecutarInsertVista(INSERT,
                    item.getOrden().getId(),
                    item.getProducto().getId(),
                    item.getTamaño() != null ? item.getTamaño().getId() : 1,
                    item.getCantidad(),
                    item.getNotas()
            );
            item.setId(id);
            return buscarPorId(id);
        } catch (SQLException e) {
            throw new DAOException("Error al guardar item: " + e.getMessage(), e);
        }
    }

    @Override
    public ItemOrden actualizar(ItemOrden item) throws DAOException {
        try {
            // Solo actualizamos campos modificables manualmente
            int filas = ejecutarUpdate(UPDATE,
                    item.getOrden().getId(),
                    item.getProducto().getId(),
                    item.getTamaño() != null ? item.getTamaño().getId() : null,
                    item.getCantidad(),
                    item.getNotas(),
                    item.getId()
            );
            if (filas == 0) throw new DAOException("Item no encontrado con ID: " + item.getId());

            // Obtenemos valores actualizados desde la BD
            return buscarPorId(item.getId());
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar item: " + e.getMessage(), e);
        }
    }

    // Eliminar permanece igual
    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Item no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar item: " + e.getMessage(), e);
        }
    }

    // Buscar por ID y listar permanecen iguales
    @Override
    public ItemOrden buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar item por ID: " + e.getMessage(), e);
        }
    }

    public List<ItemOrden> buscarPorOrdenID(Integer estadoId) throws DAOException {
        try {
            return ejecutarQuery(FIND_BY_ORDEN_ID, this::mapear, estadoId);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar órdenes por estado: " + e.getMessage(), e);
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
        ProductoDAO productoDAO = new ProductoDAO();
        TamañoProductoDAO tamañoDAO = new TamañoProductoDAO();
        OrdenDAO ordenDAO = new OrdenDAO();

        Producto producto = null;
        TamañoProducto tamaño = null;
        Orden orden = null;

        try {
            producto = productoDAO.buscarPorId(rs.getInt("producto_id"));
            tamaño = tamañoDAO.buscarPorId(rs.getInt("tamaño_id"));
            orden = ordenDAO.buscarPorId(rs.getInt("orden_id"));
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
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