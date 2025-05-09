package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.EstadoUsuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación de DAO para la entidad EstadoUsuario.
 */
public class EstadoUsuarioDAO extends AbstractDAO<EstadoUsuario, Integer> {

    // Consultas SQL
    private static final String INSERT = "INSERT INTO estado_usuario (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE estado_usuario SET nombre = ?, descripcion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM estado_usuario WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM estado_usuario WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM estado_usuario ORDER BY nombre";
    private static final String FIND_BY_NOMBRE = "SELECT * FROM estado_usuario WHERE nombre = ?";

    @Override
    public EstadoUsuario guardar(EstadoUsuario estado) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, estado.getNombre(), estado.getDescripcion());
            estado.setId(id);
            return estado;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el estado de usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoUsuario actualizar(EstadoUsuario estado) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE, estado.getNombre(), estado.getDescripcion(), estado.getId());
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el estado de usuario con ID " + estado.getId());
            }
            return estado;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el estado de usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(DELETE, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el estado de usuario con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar el estado de usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoUsuario buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapearEstado, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el estado de usuario por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EstadoUsuario> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapearEstado);
        } catch (SQLException e) {
            throw new DAOException("Error al listar todos los estados de usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un estado de usuario por su nombre.
     * @param nombre Nombre del estado a buscar
     * @return Estado encontrado o null si no existe
     * @throws DAOException Si ocurre un error al buscar
     */
    public EstadoUsuario buscarPorNombre(String nombre) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_NOMBRE, this::mapearEstado, nombre);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el estado de usuario por nombre: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto EstadoUsuario.
     * @param rs ResultSet con los datos
     * @return Objeto EstadoUsuario mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private EstadoUsuario mapearEstado(ResultSet rs) throws SQLException {
        return EstadoUsuario.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}