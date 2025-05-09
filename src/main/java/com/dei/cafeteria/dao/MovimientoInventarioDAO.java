package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.MovimientoInventario;
import com.dei.cafeteria.modelo.Producto;
import com.dei.cafeteria.modelo.TipoMovimiento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MovimientoInventarioDAO extends AbstractDAO<MovimientoInventario, Integer> {

    private static final String INSERT = "INSERT INTO movimiento_inventario (producto_id, tipo_movimiento_id, cantidad, stock_previo, stock_nuevo, referencia) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE movimiento_inventario SET producto_id=?, tipo_movimiento_id=?, cantidad=?, stock_previo=?, stock_nuevo=?, referencia=? WHERE id=?";
    private static final String DELETE = "DELETE FROM movimiento_inventario WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM movimiento_inventario WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM movimiento_inventario";

    @Override
    public MovimientoInventario guardar(MovimientoInventario movimiento) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT,
                    movimiento.getProducto().getId(),
                    movimiento.getTipoMovimiento().getId(),
                    movimiento.getCantidad(),
                    movimiento.getStockPrevio(),
                    movimiento.getStockNuevo(),
                    movimiento.getReferencia()
            );
            movimiento.setId(id);
            return movimiento;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar movimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public MovimientoInventario actualizar(MovimientoInventario movimiento) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE,
                    movimiento.getProducto().getId(),
                    movimiento.getTipoMovimiento().getId(),
                    movimiento.getCantidad(),
                    movimiento.getStockPrevio(),
                    movimiento.getStockNuevo(),
                    movimiento.getReferencia(),
                    movimiento.getId()
            );
            if (filas == 0) throw new DAOException("Movimiento no encontrado con ID: " + movimiento.getId());
            return movimiento;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar movimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Movimiento no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar movimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public MovimientoInventario buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar movimiento por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MovimientoInventario> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar movimientos: " + e.getMessage(), e);
        }
    }

    private MovimientoInventario mapear(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("producto_id"));

        TipoMovimiento tipo = new TipoMovimiento();
        tipo.setId(rs.getInt("tipo_movimiento_id"));

        return MovimientoInventario.builder()
                .id(rs.getInt("id"))
                .producto(producto)
                .tipoMovimiento(tipo)
                .cantidad(rs.getDouble("cantidad"))
                .stockPrevio(rs.getDouble("stock_previo"))
                .stockNuevo(rs.getDouble("stock_nuevo"))
                .referencia(rs.getString("referencia"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}