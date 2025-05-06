package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.ItemOrden;
import com.dei.cafeteria.modelo.Mesa;
import com.dei.cafeteria.modelo.Producto;

import java.sql.*;

public class MesaDAO {
    private Connection conexion;

    public MesaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar(Mesa mesa) throws SQLException {
        String sql = "INSERT INTO mesa (numero, disponible) VALUES (?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, mesa.getNumero());
            stmt.setBoolean(2, mesa.isDisponible());
            stmt.executeUpdate();
        }
    }

    public void actualizar(Mesa mesa) throws SQLException {
        String sql = "UPDATE mesa SET numero = ?, disponible = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, mesa.getNumero());
            stmt.setBoolean(2, mesa.isDisponible());
            stmt.setInt(3, mesa.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM mesa WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
        }
    }

    public Mesa buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM mesa WHERE id = ?";
        Mesa mesa = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                mesa = new Mesa(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getBoolean("disponible")
                );
            }
        }
        return mesa;
    }

    public Mesa buscarPorNumero(int numero) throws SQLException {
        String sql = "SELECT * FROM mesa WHERE numero = ?";
        Mesa mesa = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                mesa = new Mesa(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getBoolean("disponible")
                );
            }
        }
        return mesa;
    }

    public Mesa buscarPorDisponibilidad(boolean disponible) throws SQLException {
        String sql = "SELECT * FROM mesa WHERE disponible = ?";
        Mesa mesa = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setBoolean(1, disponible);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                mesa = new Mesa(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getBoolean("disponible")
                );
            }
        }
        return mesa;
    }

    public Mesa buscarPorDisponibilidadYNumero(boolean disponible, int numero) throws SQLException {
        String sql = "SELECT * FROM mesa WHERE disponible = ? AND numero = ?";
        Mesa mesa = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setBoolean(1, disponible);
            stmt.setInt(2, numero);
            ResultSet rs = stmt.executeQuery();
        }
        return mesa;
    }


}
