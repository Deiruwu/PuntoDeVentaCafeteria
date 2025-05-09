package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Mesa;
import com.dei.cafeteria.modelo.EstadoMesa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación de DAO para la entidad Mesa.
 */
public class MesaDAO extends AbstractDAO<Mesa, int> {

    // DAO dependiente
    private final EstadoMesaDAO estadoMesaDAO;

    // Consultas SQL
    private static final String INSERT = "INSERT INTO mesa (numero, capacidad, ubicacion, estado_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE mesa SET numero = ?, capacidad = ?, ubicacion = ?, estado_id = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM mesa WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM mesa WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM mesa ORDER BY numero";
    private static final String FIND_BY_NUMERO = "SELECT * FROM mesa WHERE numero = ?";
    private static final String FIND_BY_ESTADO = "SELECT * FROM mesa WHERE estado_id = ?";
    private static final String FIND_DISPONIBLES = "SELECT m.* FROM mesa m JOIN estado_mesa e ON m.estado_id = e.id WHERE e.nombre = 'Disponible' AND m.activo = 1 ORDER BY m.numero";

    /**
     * Constructor con dependencias.
     * @param estadoMesaDAO DAO para estados de mesa
     */
    public MesaDAO(EstadoMesaDAO estadoMesaDAO) {
        this.estadoMesaDAO = estadoMesaDAO;
    }

    @Override
    public Mesa guardar(Mesa mesa) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT,
                    mesa.getNumero(),
                    mesa.getCapacidad(),
                    mesa.getEstado().getId());
            mesa.setId(id);
            return mesa;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public Mesa actualizar(Mesa mesa) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(UPDATE,
                    mesa.getNumero(),
                    mesa.getCapacidad(),
                    mesa.getEstado().getId(),
                    mesa.getId());
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró la mesa con ID " + mesa.getId());
            }
            return mesa;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws DAOException {
        try {
            int filasAfectadas = ejecutarUpdate(DELETE, id);
            if (filasAfectadas == 0) {
                throw new DAOException("No se encontró la mesa con ID " + id);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public Mesa buscarPorId(int id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapearMesa, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la mesa por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mesa> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapearMesa);
        } catch (SQLException e) {
            throw new DAOException("Error al listar todas las mesas: " + e.getMessage(), e);
        }
    }

    /**
     * Busca una mesa por su número.
     * @param numero Número de la mesa
     * @return Mesa encontrada o null si no existe
     * @throws DAOException Si ocurre un error al buscar
     */
    public Mesa buscarPorNumero(int numero) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_NUMERO, this::mapearMesa, numero);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la mesa por número: " + e.getMessage(), e);
        }
    }

    /**
     * Lista las mesas con un estado específico.
     * @param estadoId ID del estado
     * @return Lista de mesas en el estado especificado
     * @throws DAOException Si ocurre un error al listar
     */
    public List<Mesa> buscarPorEstado(int estadoId) throws DAOException {
        try {
            return ejecutarQuery(FIND_BY_ESTADO, this::mapearMesa, estadoId);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar mesas por estado: " + e.getMessage(), e);
        }
    }


    /**
     * Lista todas las mesas disponibles.
     * @return Lista de mesas disponibles
     * @throws DAOException Si ocurre un error al listar
     */
    public List<Mesa> listarDisponibles() throws DAOException {
        try {
            return ejecutarQuery(FIND_DISPONIBLES, this::mapearMesa);
        } catch (SQLException e) {
            throw new DAOException("Error al listar mesas disponibles: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Mesa.
     * @param rs ResultSet con los datos
     * @return Objeto Mesa mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Mesa mapearMesa(ResultSet rs) throws SQLException {
        int estadoId = rs.getInt("estado_id");
        EstadoMesa estado = null;

        try {
            estado = estadoMesaDAO.buscarPorId(estadoId);
        } catch (DAOException e) {
            throw new SQLException("Error al recuperar el estado de la mesa: " + e.getMessage(), e);
        }

        return Mesa.builder()
                .id(rs.getInt("id"))
                .numero(rs.getInt("numero"))
                .capacidad(rs.getInt("capacidad"))
                .estado(estado)
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}