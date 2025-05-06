package com.dei.cafeteria.dao;

import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.RolEmpleado;
import com.dei.cafeteria.modelo.Usuario;

import java.sql.*;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {
    private Connection conexion;

    public UsuarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Optional<Usuario> autenticar(String username, String password) throws SQLException {
        String sql = "SELECT u.id AS usuario_id, u.username, u.password_hash, e.id AS empleado_id, e.nombre, e.rol " +
                "FROM usuario u JOIN empleado e ON u.empleado_id = e.id " +
                "WHERE u.username = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password_hash");

                // Verificar si la contraseña ingresada coincide con el hash almacenado
                if (BCrypt.checkpw(password, storedPasswordHash)) {
                    Empleado empleado = new Empleado(
                            rs.getInt("empleado_id"),
                            rs.getString("nombre"),
                            RolEmpleado.valueOf(rs.getString("rol"))
                    );

                    Usuario usuario = new Usuario(
                            rs.getInt("usuario_id"),
                            rs.getString("username"),
                            storedPasswordHash,
                            empleado
                    );

                    return Optional.of(usuario); // Retorna el usuario autenticado
                }
            }
        }
        return Optional.empty(); // Si no se encontró usuario o la contraseña no coincide
    }
}
