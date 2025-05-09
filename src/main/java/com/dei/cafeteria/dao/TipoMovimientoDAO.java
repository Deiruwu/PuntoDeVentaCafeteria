package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.TipoMovimiento;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TipoMovimientoDAO extends AbstractDAO<TipoMovimiento, Integer> {

    private static final String INSERT = "INSERT INTO tipo_movimiento (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE tipo_movimiento SET nombre = ?, descripcion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM tipo_movimiento WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM tipo_movimiento WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM tipo_movimiento ORDER BY nombre";

    @Override
    public TipoMovimiento guardar(TipoMovimiento tipo) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, tipo.getNombre(), tipo.getDescripcion());
            tipo.setId(id);
            return tipo;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar tipo de movimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoMovimiento actualizar(TipoMovimiento tipo) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE, tipo.getNombre(), tipo.getDescripcion(), tipo.getId());
            if (filas == 0) throw new DAOException("Tipo no encontrado con ID: " + tipo.getId());
            return tipo;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Tipo no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public TipoMovimiento buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar tipo por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoMovimiento> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar tipos: " + e.getMessage(), e);
        }
    }

    private TipoMovimiento mapear(ResultSet rs) throws SQLException {
        return TipoMovimiento.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}