package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.TamañoProducto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TamañoProductoDAO extends AbstractDAO<TamañoProducto, Integer> {

    private static final String INSERT = "INSERT INTO tamaño_producto (nombre, factor_precio, es_porcion) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE tamaño_producto SET nombre = ?, factor_precio = ?, es_porcion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM tamaño_producto WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM tamaño_producto WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM tamaño_producto ORDER BY nombre";

    @Override
    public TamañoProducto guardar(TamañoProducto tamaño) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, tamaño.getNombre(), tamaño.getFactorPrecio(), tamaño.getEsPorcion());
            tamaño.setId(id);
            return tamaño;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el tamaño: " + e.getMessage(), e);
        }
    }

    @Override
    public TamañoProducto actualizar(TamañoProducto tamaño) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE, tamaño.getNombre(), tamaño.getFactorPrecio(), tamaño.getEsPorcion(), tamaño.getId());
            if (filas == 0) throw new DAOException("Tamaño no encontrado con ID: " + tamaño.getId());
            return tamaño;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar tamaño: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Tamaño no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar tamaño: " + e.getMessage(), e);
        }
    }

    @Override
    public TamañoProducto buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar tamaño por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TamañoProducto> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar tamaños: " + e.getMessage(), e);
        }
    }

    private TamañoProducto mapear(ResultSet rs) throws SQLException {
        return TamañoProducto.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .factorPrecio(rs.getDouble("factor_precio"))
                .esPorcion(rs.getBoolean("es_porcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}