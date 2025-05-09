package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Pago;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.MetodoPago;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PagoDAO extends AbstractDAO<Pago, Integer> {

    private static final String INSERT = "INSERT INTO pago (orden_id, fecha_hora, monto, metodo_pago_id, referencia, cambio) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE pago SET orden_id=?, fecha_hora=?, monto=?, metodo_pago_id=?, referencia=?, cambio=? WHERE id=?";
    private static final String DELETE = "DELETE FROM pago WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM pago WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM pago";

    @Override
    public Pago guardar(Pago pago) throws DAOException {
        try {
            int ordenId = pago.getOrden() != null ? pago.getOrden().getId() : null;
            int metodoPagoId = pago.getMetodoPago() != null ? pago.getMetodoPago().getId() : null;
            int id = ejecutarInsert(INSERT, ordenId, pago.getFechaHora(), pago.getMonto(),
                    metodoPagoId, pago.getReferencia(), pago.getCambio());
            pago.setId(id);
            return pago;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public Pago actualizar(Pago pago) throws DAOException {
        try {
            int ordenId = pago.getOrden() != null ? pago.getOrden().getId() : null;
            int metodoPagoId = pago.getMetodoPago() != null ? pago.getMetodoPago().getId() : null;
            int filas = ejecutarUpdate(UPDATE, ordenId, pago.getFechaHora(), pago.getMonto(),
                    metodoPagoId, pago.getReferencia(), pago.getCambio(), pago.getId());
            if (filas == 0) throw new DAOException("Pago no encontrado con ID: " + pago.getId());
            return pago;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Pago no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public Pago buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar pago por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar pagos: " + e.getMessage(), e);
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        Orden orden = new Orden();
        orden.setId(rs.getInt("orden_id"));

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(rs.getInt("metodo_pago_id"));

        return Pago.builder()
                .id(rs.getInt("id"))
                .orden(orden)
                .fechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime())
                .monto(rs.getDouble("monto"))
                .metodoPago(metodoPago)
                .referencia(rs.getString("referencia"))
                .cambio(rs.getDouble("cambio"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}