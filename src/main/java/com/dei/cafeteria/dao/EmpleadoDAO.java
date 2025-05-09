package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación de DAO para la entidad Empleado.
 */
public class EmpleadoDAO extends AbstractDAO<Empleado, Integer> {

    // Consultas SQL
    private static final String INSERT = "INSERT INTO empleado (nombre, apellido, rol_id, url_imagen) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE empleado SET nombre = ?, apellido = ?, rol_id = ?, url_imagen = ?, " +
            "WHERE id = ?";
    private static final String DELETE = "DELETE FROM empleado WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM empleado WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM empleado ORDER BY apellido, nombre";

    @Override
    public Empleado guardar(Empleado empleado) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT,
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getRol());
            empleado.setId(id);
            return empleado;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public Empleado actualizar(Empleado empleado) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE,
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getRol(),
                    empleado.getId());
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el empleado con ID " + empleado.getId());
            }
            return empleado;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(DELETE, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el empleado con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar el empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public Empleado buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapearEmpleado, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el empleado por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Empleado> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapearEmpleado);
        } catch (SQLException e) {
            throw new DAOException("Error al listar todos los empleados: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Empleado.
     * @param rs ResultSet con los datos
     * @return Objeto Empleado mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        return Empleado.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .apellido(rs.getString("apellido"))

                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}