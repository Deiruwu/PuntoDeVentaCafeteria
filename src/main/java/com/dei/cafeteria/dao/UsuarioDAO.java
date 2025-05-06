package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.RolEmpleado;
import com.dei.cafeteria.modelo.Usuario;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    private Connection conexion;

    public UsuarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Usuario autenticar(String nombreUsuario, String contraseña) throws SQLException {
        String sql = "SELECT u.id AS usuario_id, u.nombre_usuario, u.contraseña, e.id AS empleado_id, e.nombre, e.rol " +
                "FROM usuario u JOIN empleado e ON u.id_empleado = e.id " +
                "WHERE u.nombre_usuario = ? AND u.contraseña = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contraseña);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Empleado empleado = new Empleado(
                        rs.getInt("empleado_id"),
                        rs.getString("nombre"),
                        RolEmpleado.valueOf(rs.getString("rol"))
                );

                return new Usuario(
                        rs.getInt("usuario_id"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contraseña"),
                        empleado
                );
            } else {
                return null;
            }
        }
    }
}

