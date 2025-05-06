package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.Mesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAO {
    private Connection conexion;

    public OrdenDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar(Orden orden) throws SQLException {
        String sql = "INSERT INTO orden (fecha, mesa, empleado, total) VALUES (?, ?, ?, ?)";

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setString(1,"fecha");
            stmt.setObject(2,"mesa");
            stmt.setObject(3,"empleado");
            stmt.setDouble(4, Double.parseDouble("total"));
            stmt.executeUpdate();
        }
    }

    public void actualizar(Orden orden) throws SQLException {
        String sql = "UPDATE orden SET fecha = ?, mesa = ?, empleado = ?, total = ? WHERE id = ?";

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setString(1,"fecha");
            stmt.setObject(2,"mesa");
            stmt.setObject(3,"empleado");
            stmt.setDouble(4, Double.parseDouble("total"));
            stmt.setInt(5, orden.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM orden WHERE id = ?";
        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, id);
        }

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Orden buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM orden WHERE id = ?";
        Orden orden = null;

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();


            if (rs.next()){
                orden = new Orden(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        (Empleado) rs.getObject("mesero"),
                        (Mesa) rs.getObject("mesa"),
                        rs.getArray("item")
                );
            }
            return orden;
        }
    }

    public List<Orden> buscarPorFecha(String desde, String hasta) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM orden");
        boolean tieneDesde = desde != null && !desde.isEmpty();
        boolean tieneHasta = hasta != null && !hasta.isEmpty();

        List<Orden> ordenes = new ArrayList<>();

        if (tieneDesde || tieneHasta) {
            sql.append(" WHERE");
            if (tieneDesde) {
                sql.append(" fecha >= ?");
            }
            if (tieneDesde && tieneHasta) {
                sql.append(" AND");
            }
            if (tieneHasta) {
                sql.append(" fecha <= ?");
            }
        }

        try (PreparedStatement stmt = conexion.prepareStatement(sql.toString())) {
            int index = 1;

            if (tieneDesde) {
                stmt.setString(index++, desde);
            }
            if (tieneHasta) {
                stmt.setString(index++, hasta);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Orden orden = new Orden(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        (Empleado) rs.getObject("mesero"),
                        (Mesa) rs.getObject("mesa"),
                        rs.getArray("item")
                );
                ordenes.add(orden);
            }
        }
        return ordenes;
    }

    public List<Orden> buscarPorMesero(int id) throws SQLException {
        String sql = "SELECT * FROM orden WHERE mesero_id = ?";
        List<Orden> ordenes = new ArrayList<>();

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordenes.add(new Orden(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        (Empleado) rs.getObject("mesero"),
                        (Mesa) rs.getObject("mesa"),
                        rs.getArray("item")
                ));
            }
        }
        return ordenes;
    }

    public List<Orden> buscarPorMesa(int id) throws SQLException {
        String sql = "SELECT * FROM orden WHERE mesa_id = ?";
        List<Orden> ordenes = new ArrayList<>();

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordenes.add(new Orden(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        (Empleado) rs.getObject("mesero"),
                        (Mesa) rs.getObject("mesa"),
                        rs.getArray("item")
                ));
            }
        }
        return ordenes;
    }

    public List<Orden> listarTodos() throws SQLException {
        String sql = "SELECT * FROM orden";
        List<Orden> ordenes = new ArrayList<>();

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ordenes.add(new Orden(
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        (Empleado) rs.getObject("mesero"),
                        (Mesa) rs.getObject("mesa"),
                        rs.getArray("item")
                ));
            }
        }
        return ordenes;
    }
}
