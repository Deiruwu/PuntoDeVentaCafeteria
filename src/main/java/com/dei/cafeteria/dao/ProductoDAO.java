package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Connection conexion;

    public ProductoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (nombre, precio, stock, codigo_barras) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getCodigoBarras());
            stmt.executeUpdate();
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE producto SET nombre = ?, precio = ?, stock = ?, codigo_barras = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getCodigoBarras());
            stmt.setInt(5, producto.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM producto WHERE id = ?";
        Producto producto = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("codigo_barras")
                );
            }
        }
        return producto;
    }

    public Producto buscarPorCodigoBarras(String codigoBarras) throws SQLException {
        String sql = "SELECT * FROM producto WHERE codigo_barras = ?";
        Producto producto = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("codigo_barras")
                );
            }
        }
        return producto;
    }


    public List<Producto> listarTodos() throws SQLException {
        String sql = "SELECT * FROM producto";
        List<Producto> productos = new ArrayList<>();

        try (Statement stmt = conexion.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("codigo_barras")
                );
                productos.add(producto);
            }
        }
        return productos;
    }
}
