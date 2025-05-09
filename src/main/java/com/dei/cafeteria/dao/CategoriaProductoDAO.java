package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.CategoriaProducto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoriaProductoDAO extends AbstractDAO<CategoriaProducto, Integer> {

    private static final String INSERT = "INSERT INTO categoria_producto (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE categoria_producto SET nombre = ?, descripcion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM categoria_producto WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM categoria_producto WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM categoria_producto ORDER BY nombre";

    @Override
    public CategoriaProducto guardar(CategoriaProducto categoria) throws DAOException {
        try {
            int id = ejecutarInsert(INSERT, categoria.getNombre(), categoria.getDescripcion());
            categoria.setId(id);
            return categoria;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar la categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public CategoriaProducto actualizar(CategoriaProducto categoria) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE, categoria.getNombre(), categoria.getDescripcion(), categoria.getId());
            if (filas == 0) throw new DAOException("Categoría no encontrada con ID: " + categoria.getId());
            return categoria;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Categoría no encontrada con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar categoría: " + e.getMessage(), e);
        }
    }

    @Override
    public CategoriaProducto buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar categoría por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CategoriaProducto> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar categorías: " + e.getMessage(), e);
        }
    }

    private CategoriaProducto mapear(ResultSet rs) throws SQLException {
        return CategoriaProducto.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}