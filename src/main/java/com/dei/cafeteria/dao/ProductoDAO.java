package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Producto;
import com.dei.cafeteria.modelo.CategoriaProducto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductoDAO extends AbstractDAO<Producto, Integer> {

    private static final String INSERT = "INSERT INTO producto (nombre, precio_base, aplica_iva, descripcion, categoria_id, disponible, stock_actual, stock_minimo, imagen_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE producto SET nombre=?, precio_base=?, aplica_iva=?, descripcion=?, categoria_id=?, disponible=?, stock_actual=?, stock_minimo=?, imagen_url=? WHERE id=?";
    private static final String DELETE = "DELETE FROM producto WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM producto WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM producto";

    @Override
    public Producto guardar(Producto producto) throws DAOException {
        try {
            int categoriaId = producto.getCategoria() != null ? producto.getCategoria().getId() : null;
            int id = ejecutarInsert(INSERT, producto.getNombre(), producto.getPrecioBase(), producto.getAplicaIva(),
                    producto.getDescripcion(), categoriaId, producto.getDisponible(), producto.getStockActual(),
                    producto.getStockMinimo(), producto.getImagenUrl());
            producto.setId(id);
            return producto;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto actualizar(Producto producto) throws DAOException {
        try {
            int categoriaId = producto.getCategoria() != null ? producto.getCategoria().getId() : null;
            int filas = ejecutarUpdate(UPDATE, producto.getNombre(), producto.getPrecioBase(), producto.getAplicaIva(),
                    producto.getDescripcion(), categoriaId, producto.getDisponible(), producto.getStockActual(),
                    producto.getStockMinimo(), producto.getImagenUrl(), producto.getId());
            if (filas == 0) throw new DAOException("Producto no encontrado con ID: " + producto.getId());
            return producto;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id);
            if (filas == 0) throw new DAOException("Producto no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto buscarPorId(Integer id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id);
        } catch (SQLException e) {
            throw new DAOException("Error al buscar producto por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar productos: " + e.getMessage(), e);
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setId(rs.getInt("categoria_id"));

        return Producto.builder()
                .id(rs.getInt("id"))
                .nombre(rs.getString("nombre"))
                .precioBase(rs.getDouble("precio_base"))
                .aplicaIva(rs.getBoolean("aplica_iva"))
                .descripcion(rs.getString("descripcion"))
                .categoria(categoria)
                .disponible(rs.getBoolean("disponible"))
                .stockActual(rs.getDouble("stock_actual"))
                .stockMinimo(rs.getDouble("stock_minimo"))
                .imagenUrl(rs.getString("imagen_url"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}