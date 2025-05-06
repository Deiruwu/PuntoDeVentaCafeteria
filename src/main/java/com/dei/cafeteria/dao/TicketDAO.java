package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Orden;
import com.dei.cafeteria.modelo.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketDAO {
    private Connection conexion;

    public TicketDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar() throws SQLException {
        String sql = "INSERT INTO ticket (id_orden, id_empleado, fecha) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt("id_orden"));
            stmt.setInt(2, Integer.parseInt("id_empleado"));
            stmt.setString(3, "fecha");
            stmt.executeUpdate();
        }
    }

    public void actualizar() throws SQLException{
        String sql = "UPDATE ticket SET id_orden = ?, id_empleado = ?, fecha = ? WHERE id = ?";

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt("id_orden"));
            stmt.setInt(2, Integer.parseInt("id_empleado"));
            stmt.setString(3, "fecha");
            stmt.setInt(4, Integer.parseInt("id"));
            stmt.executeUpdate();
        }
    }

    public void eliminar() throws SQLException{
        String sql = "DELETE FROM ticket WHERE id = ?";
        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt("id"));
            stmt.executeUpdate();
        }
    }

    public Ticket buscarPorId() throws SQLException{
        String sql = "SELECT * FROM ticket WHERE id = ?";
        Ticket ticket = null;

        try(PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt("id"));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                ticket = new Ticket(
                        rs.getInt("id"),
                        (Orden) rs.getObject("ordeb"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getDouble("total")
                );
            }
        }
        return ticket;
    }
}
