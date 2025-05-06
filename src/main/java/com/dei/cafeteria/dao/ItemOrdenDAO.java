package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemOrdenDAO {
    private Connection conexion;

    public ItemOrdenDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar(ItemOrden itemOrden) throws SQLException {
        String sql = "INSERT INTO empleado (producto, cantidad, subtotal) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, itemOrden.getProducto().getNombre());
            stmt.setInt(2, itemOrden.getCantidad());
            stmt.setDouble(3, itemOrden.getSubtotal());
            stmt.executeUpdate();
        }
    }

    public void actualizar(ItemOrden itemOrden) throws SQLException {
        String sql = "UPDATE empleado SET producto = ?, cantidad = ?, subtotal = ? WHERE id = ?";

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setString(1, itemOrden.getProducto().getNombre());
            stmt.setInt(2, itemOrden.getCantidad());
            stmt.setDouble(3, itemOrden.getSubtotal());
            stmt.setInt(4, itemOrden.getId());
            stmt.executeUpdate();
        }
    }


    public void eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public ItemOrden buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empleado WHERE id = ?";
        ItemOrden itemOrden = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                itemOrden = new ItemOrden(
                        (Producto) rs.getObject("producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("subtotal")
                );
            }
        }
        return itemOrden;
    }

    public List<ItemOrden> listarTodos() throws SQLException {
        String sql = "SELECT * FROM empleado";
        List<ItemOrden> itemOrdenes = new ArrayList<>();

        try (Statement stmt = conexion.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                itemOrdenes.add(new ItemOrden(
                        (Producto) rs.getObject("producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("subtotal")
                ));
            }
        }
        return itemOrdenes;
    }
}
