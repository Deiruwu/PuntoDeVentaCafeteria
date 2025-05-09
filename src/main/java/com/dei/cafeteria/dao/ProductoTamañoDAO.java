package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.ProductoTamaño;
import com.dei.cafeteria.modelo.ProductoTamañoId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductoTamañoDAO extends AbstractDAO<ProductoTamaño, ProductoTamañoId> {

    private static final String INSERT = "INSERT INTO Producto_Tamaño (id_producto, id_tamaño, activo, fecha_creacion, fecha_actualizacion) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE Producto_Tamaño SET activo = ?, fecha_actualizacion = ? WHERE id_producto = ? AND id_tamaño = ?";
    private static final String DELETE = "DELETE FROM Producto_Tamaño WHERE id_producto = ? AND id_tamaño = ?";
    private static final String FIND_BY_ID = "SELECT * FROM Producto_Tamaño WHERE id_producto = ? AND id_tamaño = ?";
    private static final String FIND_ALL = "SELECT * FROM Producto_Tamaño";

    @Override
    public ProductoTamaño guardar(ProductoTamaño productoTamaño) throws DAOException {
        try {
            ejecutarInsert(INSERT,
                    productoTamaño.getId().getIdProducto(),
                    productoTamaño.getId().getIdTamaño(),
                    productoTamaño.getActivo(),
                    productoTamaño.getFechaCreacion(),
                    productoTamaño.getFechaActualizacion());
            return productoTamaño;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar ProductoTamaño: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoTamaño actualizar(ProductoTamaño productoTamaño) throws DAOException {
        try {
            int filas = ejecutarUpdate(UPDATE,
                    productoTamaño.getActivo(),
                    productoTamaño.getFechaActualizacion(),
                    productoTamaño.getId().getIdProducto(),
                    productoTamaño.getId().getIdTamaño());
            if (filas == 0) throw new DAOException("ProductoTamaño no encontrado con ID: " + productoTamaño.getId());
            return productoTamaño;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar ProductoTamaño: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(ProductoTamañoId id) throws DAOException {
        try {
            int filas = ejecutarUpdate(DELETE, id.getIdProducto(), id.getIdTamaño());
            if (filas == 0) throw new DAOException("ProductoTamaño no encontrado con ID: " + id);
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar ProductoTamaño: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoTamaño buscarPorId(ProductoTamañoId id) throws DAOException {
        try {
            return ejecutarQueryUnico(FIND_BY_ID, this::mapear, id.getIdProducto(), id.getIdTamaño());
        } catch (SQLException e) {
            throw new DAOException("Error al buscar ProductoTamaño por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductoTamaño> listarTodos() throws DAOException {
        try {
            return ejecutarQuery(FIND_ALL, this::mapear);
        } catch (SQLException e) {
            throw new DAOException("Error al listar ProductoTamaño: " + e.getMessage(), e);
        }
    }

    private ProductoTamaño mapear(ResultSet rs) throws SQLException {
        return ProductoTamaño.builder()
                .id(new ProductoTamañoId(
                        rs.getInt("id_producto"),
                        rs.getInt("id_tamaño")))
                .activo(rs.getBoolean("activo"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .fechaActualizacion(rs.getTimestamp("fecha_actualizacion").toLocalDateTime())
                .build();
    }
}
