package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.EstadoOrden;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrdenDAO extends AbstractDAO<Orden, Integer> {

    private static final String INSERT = "INSERT INTO orden (fecha_hora, estado_id, subtotal, iva, total, notas) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE orden SET fecha_hora=?, estado_id=?, subtotal=?, iva=?, total=?, notas=? WHERE id=?";
    private static final String DELETE = "DELETE FROM orden WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM orden WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM orden";

    @Override
    public Orden guardar(Orden orden) throws DAOException {
        try {
            int estadoId = orden.getEstado() != null ? orden.getEstado().getId() : null;
            int id = ejecutarInsert(INSERT, orden.getFechaHora(), estadoId, orden.getSubtotal(),
                    orden.getIva(), orden.getTotal(), orden.getNotas());
            orden.setId(id);
            return orden;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar orden: " + e.getMessage(), e);
        }
    }

    @Override
    public Orden actualizar(Orden orden) throws DAOException {
        try {
            int estadoId = orden.getEstado() != null ? orden.getEstado().getId() : null;
            int filas = ejecutarUpdate(UPDATE, orden.getFechaHora(), estadoId, orden.getSubtotal(),
                    orden.getIva(), orden.getTotal(), orden.getNotas(), orden.getId());
            if (filas == 0) throw new DAOException("Orden no encontrada con ID: " + orden.getId());
            return orden;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar orden: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Orden no encontrada con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar orden: " + e.getMessage(), e);
        }
    }

    @Override
    public Orden buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar orden por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Orden> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar órdenes: " + e.getMessage(), e);
        }
    }

    private Orden mapear(ResultSet rs) throws SQLException {
        EstadoOrden estado = new EstadoOrden();
        estado.setId(rs.getInt("estado_id"));

        return Orden.builder()
                .id(rs.getInt("id"))
                .fechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime())
                .estado(estado)
                .subtotal(rs.getDouble("subtotal"))
                .iva(rs.getDouble("iva"))
                .total(rs.getDouble("total"))
                .notas(rs.getString("notas"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}