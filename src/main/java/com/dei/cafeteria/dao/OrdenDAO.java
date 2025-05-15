package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.Mesa;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.EstadoOrden;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrdenDAO extends AbstractDAO<Orden, Integer> {

    // Campos eliminados: fecha_hora (tiene DEFAULT), subtotal, iva, total (calculados)
    private static final String INSERT = "INSERT INTO orden (mesa_id, mesero_id, estado_id, notas) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE orden SET estado_id=?, notas=? WHERE id=?";
    private static final String DELETE = "DELETE FROM orden WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM orden WHERE id=?";
    private static final String FIND_BY_ESTADO = "SELECT * FROM orden WHERE estado_id=?";
    private static final String FIND_ALL = "SELECT * FROM orden";

    @Override
    public Orden guardar(Orden orden) throws DAOException {
        try {
            // Campos requeridos: mesa_id y mesero_id (no pueden ser nulos)
            int id = ejecutarInsert(INSERT,
                    orden.getMesa().getId(),
                    orden.getMesero().getId(),
                    orden.getEstado() != null ? orden.getEstado().getId() : 1, // Default 1=PENDIENTE
                    orden.getNotas()
            );
            orden.setId(id);

            // Obtenemos los valores calculados desde la BD
            return buscarPorId(id);
        } catch (SQLException e) {
            throw new DAOException("Error al guardar orden: " + e.getMessage(), e);
        }
    }

    @Override
    public Orden actualizar(Orden orden) throws DAOException {
        try {
            // Solo actualizamos campos modificables: estado y notas
            int filas = ejecutarUpdate(UPDATE,
                    orden.getEstado().getId(),
                    orden.getNotas(),
                    orden.getId()
            );
            if (filas == 0) throw new DAOException("Orden no encontrada con ID: " + orden.getId());

            return buscarPorId(orden.getId());
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar orden: " + e.getMessage(), e);
        }
    }

    // Eliminar permanece igual
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

    public List<Orden> buscarPorEstado(Integer estadoId) throws DAOException {
        try {
            return ejecutarQuery(FIND_BY_ESTADO, this::mapear, estadoId);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar órdenes por estado: " + e.getMessage(), e);
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

        Mesa mesa = new Mesa();
        mesa.setId(rs.getInt("mesa_id"));

        Empleado mesero = new Empleado();
        mesero.setId(rs.getInt("mesero_id"));

        return Orden.builder()
                .id(rs.getInt("id"))
                .mesa(mesa)
                .mesero(mesero)
                .fechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime()) // Lo obtiene de la BD
                .estado(estado)
                .subtotal(rs.getDouble("subtotal"))  // Calculado por triggers
                .iva(rs.getDouble("iva"))            // Calculado por triggers
                .total(rs.getDouble("total"))         // Calculado por triggers
                .notas(rs.getString("notas"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}