package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.RolEmpleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {


    private Connection conexion;

    public EmpleadoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public void guardar(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleado (nombre, rol) VALUES (?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getRol().name());
            stmt.executeUpdate();
        }
    }

    public void actualizar(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleado SET nombre = ?, rol = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getRol().name());
            stmt.setInt(3, empleado.getId());
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
    public Empleado buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empleado WHERE id = ?";
        Empleado empleado = null;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                empleado = new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        RolEmpleado.valueOf(rs.getString("rol"))
                );
            }
        }
        return empleado;
    }

    public List<Empleado> listarTodos() throws SQLException {
        String sql = "SELECT * FROM empleado";
        List<Empleado> empleados = new ArrayList<>();

        try (Statement stmt = conexion.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Empleado empleado = new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        RolEmpleado.valueOf(rs.getString("rol"))
                );
                empleados.add(empleado);
            }
        }
        return empleados;
    }
}
