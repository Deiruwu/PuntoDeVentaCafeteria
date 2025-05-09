package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.EstadoMesa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación de DAO para la entidad EstadoMesa.
 */
public class EstadoMesaDAO extends AbstractDAO<EstadoMesa, Integer> {

    // Consultas SQL
    private static final String INSERT = "INSERT INTO estado_mesa (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE estado_mesa SET nombre = ?, descripcion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM estado_mesa WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM estado_mesa WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM estado_mesa ORDER BY nombre";
    private static final String FIND_BY_NOMBRE = "SELECT * FROM estado_mesa WHERE nombre = ?";

    @Override
    public EstadoMesa guardar(EstadoMesa estado) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, estado.getNombre(), estado.getDescripcion());
            estado.setId(id);
            return estado;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el estado de mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoMesa actualizar(EstadoMesa estado) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE, estado.getNombre(),
                    estado.getDescripcion(), estado.getId());
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el estado de mesa con ID " + estado.getId());
            }
            return estado;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el estado de mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(DELETE, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el estado de mesa con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar el estado de mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoMesa buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapearEstadoMesa, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el estado de mesa por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EstadoMesa> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapearEstadoMesa);
        } catch (SQLException e) {
            throw new DAOException("Error al listar todos los estados de mesa: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un estado de mesa por su nombre.
     * @param nombre Nombre del estado a buscar
     * @return Estado encontrado o null si no existe
     * @throws DAOException Si ocurre un error al buscar
     */
    public EstadoMesa buscarPorNombre(String nombre) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_NOMBRE, this::mapearEstadoMesa, nombre);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el estado de mesa por nombre: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto EstadoMesa.
     * @param rs ResultSet con los datos
     * @return Objeto EstadoMesa mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private EstadoMesa mapearEstadoMesa(ResultSet rs) throws SQLException {
        return EstadoMesa.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}