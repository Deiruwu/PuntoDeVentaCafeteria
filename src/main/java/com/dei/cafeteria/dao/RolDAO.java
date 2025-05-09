package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Rol;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.*;

/**
 * Implementación de DAO para la entidad Rol.
 */
public class RolDAO extends AbstractDAO<Rol, Integer> {

    // Consultas SQL
    private static final String INSERT = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE rol SET nombre = ?, descripcion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM rol WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM rol WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM rol ORDER BY nombre";
    private static final String FIND_BY_NOMBRE = "SELECT * FROM rol WHERE nombre = ?";

    @Override
    public Rol guardar(Rol rol) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, rol.getNombre(), rol.getDescripcion());
            rol.setId(id);
            return rol;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el rol: " + e.getMessage(), e);
        }
    }

    @Override
    public Rol actualizar(Rol rol) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE, rol.getNombre(), rol.getDescripcion(), rol.getId());
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el rol con ID " + rol.getId());
            }
            return rol;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el rol: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(DELETE, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el rol con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar el rol: " + e.getMessage(), e);
        }
    }

    @Override
    public Rol buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapearRol, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el rol por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Rol> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapearRol);
        } catch (SQLException e) {
            throw new DAOException("Error al listar todos los roles: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un rol por su nombre.
     * @param nombre Nombre del rol a buscar
     * @return Rol encontrado o null si no existe
     * @throws DAOException Si ocurre un error al buscar
     */
    public Rol buscarPorNombre(String nombre) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_NOMBRE, this::mapearRol, nombre);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el rol por nombre: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Rol.
     * @param rs ResultSet con los datos
     * @return Objeto Rol mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Rol mapearRol(ResultSet rs) throws SQLException {
        return Rol.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}