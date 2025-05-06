package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAO {
    private Connection conexion;

    public OrdenDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar(Orden orden) throws SQLException {
        String sql = "INSERT INTO orden (fecha_hora, id_mesa, id_mesero) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(orden.getFechaHora()));
            stmt.setInt(2, orden.getMesa().getId());
            stmt.setInt(3, orden.getMesero().getId());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                orden.setId(generatedKeys.getInt(1));
            }

            ItemOrdenDAO itemDAO = new ItemOrdenDAO(conexion);
            for (ItemOrden item : orden.getItems()) {
                item.setId(orden.getId());
                itemDAO.guardar(item);
            }
        }
    }

    public void actualizar(Orden orden) throws SQLException {
        String sql = "UPDATE orden SET fecha_hora = ?, id_mesa = ?, id_mesero = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(orden.getFechaHora()));
            stmt.setInt(2, orden.getMesa().getId());
            stmt.setInt(3, orden.getMesero().getId());
            stmt.setInt(4, orden.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM orden WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Orden buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM orden WHERE id = ?";
        Orden orden = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int ordenId = rs.getInt("id");
                Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                Empleado mesero = new EmpleadoDAO(conexion).buscarPorId(rs.getInt("id_mesero"));
                Mesa mesa = new MesaDAO(conexion).buscarPorId(rs.getInt("id_mesa"));

                // Recuperar los ItemOrden a través de los IDs
                List<ItemOrden> items = obtenerItemsDeOrden(ordenId);

                orden = new Orden(ordenId, fechaHora.toLocalDateTime(), mesero, mesa, items);
            }
        }
        return orden;
    }

    public List<Orden> listarTodos() throws SQLException {
        List<Orden> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM orden";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Orden orden = buscarPorId(rs.getInt("id"));
                if (orden != null) ordenes.add(orden);
            }
        }
        return ordenes;
    }

    public List<ItemOrden> obtenerItemsDeOrden(int ordenId) throws SQLException {
        List<ItemOrden> items = new ArrayList<>();
        String sql = "SELECT * FROM item_orden WHERE orden_id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, ordenId); // Pasamos el ID de la orden
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productoId = rs.getInt("producto_id");
                Producto producto = new ProductoDAO(conexion).buscarPorId(productoId);

                // Crear un objeto ItemOrden
                ItemOrden item = new ItemOrden(
                        rs.getInt("id"),
                        producto,
                        rs.getInt("cantidad"),
                        rs.getDouble("subtotal")
                );
                items.add(item);
            }
        }
        return items;
    }
}
