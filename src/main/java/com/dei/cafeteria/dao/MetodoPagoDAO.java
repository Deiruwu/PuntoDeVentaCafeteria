package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.MetodoPago;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MetodoPagoDAO extends AbstractDAO<MetodoPago, Integer> {

    private static final String INSERT = "INSERT INTO metodo_pago (nombre, descripcion, requiere_referencia) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE metodo_pago SET nombre=?, descripcion=?, requiere_referencia=? WHERE id=?";
    private static final String DELETE = "DELETE FROM metodo_pago WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM metodo_pago WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM metodo_pago ORDER BY nombre";

    @Override
    public MetodoPago guardar(MetodoPago metodo) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT,
                    metodo.getNombre(),
                    metodo.getDescripcion(),
                    metodo.getRequiereReferencia()
            );
            metodo.setId(id);
            return metodo;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar método de pago: " + e.getMessage(), e);
        }
    }

    @Override
    public MetodoPago actualizar(MetodoPago metodo) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE,
                    metodo.getNombre(),
                    metodo.getDescripcion(),
                    metodo.getRequiereReferencia(),
                    metodo.getId()
            );
            if (filas == 0) throw new DAOException("Método no encontrado con ID: " + metodo.getId());
            return metodo;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar método: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Método no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar método: " + e.getMessage(), e);
        }
    }

    @Override
    public MetodoPago buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar método por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MetodoPago> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar métodos: " + e.getMessage(), e);
        }
    }

    private MetodoPago mapear(ResultSet rs) throws SQLException {
        return MetodoPago.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .requiereReferencia(rs.getBoolean("requiere_referencia"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}