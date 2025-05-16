package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.Pago;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.MetodoPago;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class PagoDAO extends AbstractDAO<Pago, Integer> {

    private static final String INSERT = "INSERT INTO pago (orden_id, fecha_hora, monto, metodo_pago_id, referencia, cambio, cajero_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE pago SET orden_id=?, fecha_hora=?, monto=?, metodo_pago_id=?, referencia=?, cambio=? WHERE id=?";
    private static final String DELETE = "DELETE FROM pago WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM pago WHERE id=?";
    private static final String FIND_BY_ORDEN_ID = "SELECT * FROM pago WHERE orden_id=?";
    private static final String FIND_BY_FECHA_RANGO = "SELECT * FROM pago WHERE fecha_hora BETWEEN ? AND ?";
    private static final String FIND_ALL = "SELECT * FROM pago";
    private static final Log log = LogFactory.getLog(PagoDAO.class);

    @Override
    public Pago guardar(Pago pago) throws DAOException {
        try {
            String referencia = pago.getReferencia() != null ? pago.getReferencia() : "";

            int id = ejecutarInsert(INSERT, pago.getOrdenId(), pago.getFechaHora(), pago.getMonto(),
                    pago.getMetodoPago().getId(), referencia, pago.getCambio(), pago.getCajero().getId());
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

    public List<Pago> buscarPorOrdenID(Integer ordenId) throws DAOException {
        try {
            return ejecutarQuery(FIND_BY_ORDEN_ID, this::mapear, ordenId);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar pagos por orden ID: " + e.getMessage(), e);
        }
    }

    public List<Pago> buscarPorRangoFechas(String inicio, String fin) throws DAOException {
        try {
            return ejecutarQueryFechas(FIND_BY_FECHA_RANGO, this::mapear,
                    Timestamp.valueOf(inicio), Timestamp.valueOf(fin));

        } catch (SQLException e) {
            throw new DAOException("Error al buscar pagos por rango de fechas: " + e.getMessage(), e);
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
        OrdenDAO ordenDAO= new OrdenDAO();
        MetodoPagoDAO metodoPagoDAO = new MetodoPagoDAO();
        RolDAO rolDAO = new RolDAO();
        EmpleadoDAO cajeroDAO = new EmpleadoDAO(rolDAO);
        Orden orden = null;
        MetodoPago metodoPago = null;
        Empleado cajero = null;
        try {
            orden = ordenDAO.buscarPorId(rs.getInt("orden_id"));
            metodoPago = metodoPagoDAO.buscarPorId( rs.getInt("metodo_pago_id"));
            cajero = cajeroDAO.buscarPorId(rs.getInt("cajero_id"));
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return Pago.builder()
                .id(rs.getInt("id"))
                .orden(orden)
                .fechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime())
                .monto(rs.getDouble("monto"))
                .metodoPago(metodoPago)
                .referencia(rs.getString("referencia"))
                .cambio(rs.getDouble("cambio"))
                .cajero(cajero)
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}