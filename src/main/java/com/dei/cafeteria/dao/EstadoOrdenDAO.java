package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.EstadoOrden;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EstadoOrdenDAO extends AbstractDAO<EstadoOrden, Integer> {

    private static final String INSERT = "INSERT INTO estado_orden (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE estado_orden SET nombre=?, descripcion=? WHERE id=?";
    private static final String DELETE = "DELETE FROM estado_orden WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM estado_orden WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM estado_orden ORDER BY nombre";

    @Override
    public EstadoOrden guardar(EstadoOrden estado) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, estado.getNombre(), estado.getDescripcion());
            estado.setId(id);
            return estado;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar estado: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoOrden actualizar(EstadoOrden estado) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE, estado.getNombre(), estado.getDescripcion(), estado.getId());
            if (filas == 0) throw new DAOException("Estado no encontrado con ID: " + estado.getId());
            return estado;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar estado: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Estado no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar estado: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoOrden buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar estado por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EstadoOrden> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar estados: " + e.getMessage(), e);
        }
    }

    private EstadoOrden mapear(ResultSet rs) throws SQLException {
        return EstadoOrden.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}