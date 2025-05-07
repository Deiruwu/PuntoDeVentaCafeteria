package com.dei.cafeteria;

import com.dei.cafeteria.dao.UsuarioDAO;
import com.dei.cafeteria.modelo.Usuario;
import com.dei.cafeteria.utilidad.DatabaseConnection;

import java.sql.Connection;
import java.util.Optional;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        // Crear la conexión a la base de datos
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Crear el DAO de usuarios
            UsuarioDAO usuarioDao = new UsuarioDAO(conn);

            // Leer los datos de login desde la consola
            Scanner scanner = new Scanner(System.in);
            System.out.println("Nombre de usuario: ");
            String username = scanner.nextLine();
            System.out.println("Contraseña: ");
            String passwordHash = scanner.nextLine();  // Aquí deberías poner el hash de la contraseña

            // Intentar autenticar al usuario
            Optional<Usuario> usuarioOpt = usuarioDao.autenticar(username, passwordHash);

            // Verificar si el login fue exitoso
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();  // Obtener el Usuario desde el Optional
                System.out.println("¡Login exitoso!");
                // Aquí puedes continuar con lo que necesites, por ejemplo:
                System.out.println("Bienvenida, " + usuario.getEmpleado().getNombre());
            } else {
                System.out.println("Login fallido. Nombre de usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
