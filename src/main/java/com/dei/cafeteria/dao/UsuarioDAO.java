package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Usuario;
import com.dei.cafeteria.modelo.Rol;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.EstadoUsuario;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Implementación de DAO para la entidad Usuario.
 */
public class UsuarioDAO extends AbstractDAO<Usuario, Integer> {
    // DAOs dependientes
    private final EmpleadoDAO empleadoDAO;
    private final EstadoUsuarioDAO estadoUsuarioDAO;

    // Consultas SQL
    private static final String INSERT = "INSERT INTO usuario (nombre_usuario, hash_contraseña, empleado_id, ultimo_login) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE usuario SET nombre_usuario = ?, hash_contraseña = ?, " +
            "empleado_id = ?, estado_id = ?, ultimo_login = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM usuario WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM usuario WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM usuario ORDER BY nombre_usuario";
    private static final String FIND_BY_USERNAME = "SELECT * FROM usuario WHERE nombre_usuario = ?"; // Usando índice idx_usuario_nombre_usuario
    private static final String FIND_BY_EMPLEADO = "SELECT * FROM usuario WHERE empleado_id = ?";
    private static final String FIND_BY_ESTADO = "SELECT * FROM usuario WHERE estado_id = ?";
    private static final String UPDATE_ULTIMO_LOGIN = "UPDATE usuario SET ultimo_login = ? WHERE id = ?";

    /**
     * Constructor con dependencias.
     * @param empleadoDAO DAO para empleados
     * @param estadoUsuarioDAO DAO para estados de usuario
     */
    public UsuarioDAO(EmpleadoDAO empleadoDAO, EstadoUsuarioDAO estadoUsuarioDAO) {
        this.empleadoDAO = empleadoDAO;
        this.estadoUsuarioDAO = estadoUsuarioDAO;
    }


    @Override
    public Usuario guardar(Usuario usuario) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT,
                    usuario.getNombreUsuario(),
                    usuario.getHashContraseña(),
                    usuario.getEmpleado().getId(),
                    usuario.getUltimoLogin());
            usuario.setId(id);
            return usuario;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario actualizar(Usuario usuario) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE,
                    usuario.getNombreUsuario(),
                    usuario.getHashContraseña(),
                    usuario.getEmpleado().getId(),
                    usuario.getEstado().getId(),
                    usuario.getUltimoLogin(),
                    usuario.getId());
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el usuario con ID " + usuario.getId());
            }
            return usuario;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(DELETE, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el usuario con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar el usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapearUsuario, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el usuario por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapearUsuario);
        } catch (SQLException e) {
            throw new DAOException("Error al listar todos los usuarios: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * @param nombreUsuario Nombre de usuario a buscar
     * @return Usuario encontrado o null si no existe
     * @throws DAOException Si ocurre un error al buscar
     */
    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_USERNAME, this::mapearUsuario, nombreUsuario);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el usuario por nombre de usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Busca los usuarios asignados a un empleado específico.
     * @param empleadoId ID del empleado
     * @return Lista de usuarios del empleado
     * @throws DAOException Si ocurre un error al buscar
     */
    public Usuario buscarPorEmpleado(int empleadoId) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_EMPLEADO, this::mapearUsuario, empleadoId);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuarios por empleado: " + e.getMessage(), e);
        }
    }

    /**
     * Busca los usuarios en un estado específico.
     * @param estadoId ID del estado
     * @return Lista de usuarios en el estado especificado
     * @throws DAOException Si ocurre un error al buscar
     */
    public List<Usuario> buscarPorEstado(int estadoId) throws DAOException {
        try {
            return ejecutarQuery(FIND_BY_ESTADO, this::mapearUsuario, estadoId);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuarios por estado: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza la fecha del último acceso de un usuario.
     * @param id ID del usuario
     * @param ultimoAcceso Fecha del último acceso
     * @throws DAOException Si ocurre un error al actualizar
     */
    public void actualizarUltimoAcceso(int id, Timestamp ultimoAcceso) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE_ULTIMO_LOGIN, ultimoAcceso, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró el usuario con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el último acceso del usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Usuario.
     * @param rs ResultSet con los datos
     * @return Objeto Usuario mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        int empleadoId = rs.getInt("empleado_id");
        int estadoId = rs.getInt("estado_id");

        Rol rol = null;
        Empleado empleado = null;
        EstadoUsuario estado = null;

        try {
            empleado = empleadoDAO.buscarPorId(empleadoId);
            estado = estadoUsuarioDAO.buscarPorId(estadoId);
        } catch (DAOException e) {
            throw new SQLException("Error al recuperar las relaciones del usuario: " + e.getMessage(), e);
        }

        return Usuario.builder()
                .id(rs.getInt("id"))
                .empleado(empleado)
                .nombreUsuario(rs.getString("nombre_usuario"))
                .hashContraseña(rs.getString("hash_contraseña"))
                .ultimoLogin(
                        rs.getTimestamp("ultimo_login") != null
                                ? rs.getTimestamp("ultimo_login").toLocalDateTime()
                                : null)
                .estado(estado)
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}