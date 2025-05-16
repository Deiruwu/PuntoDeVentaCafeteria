package com.dei.cafeteria.util;

import com.dei.cafeteria.dao.EmpleadoDAO;
import com.dei.cafeteria.dao.EstadoUsuarioDAO;
import com.dei.cafeteria.dao.RolDAO;
import com.dei.cafeteria.dao.UsuarioDAO;
import com.dei.cafeteria.modelo.Empleado;
import com.dei.cafeteria.modelo.EstadoUsuario;
import com.dei.cafeteria.modelo.Rol;
import com.dei.cafeteria.modelo.Usuario;
import com.dei.cafeteria.servicios.ServicioDeAutentificacion;

import java.util.Scanner;
import java.time.LocalDateTime;

public class TestApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Asumo que tienes instancias de DAOs creadas e inyectadas:
        RolDAO rolDAO = new RolDAO(); // o como corresponda crear
        EmpleadoDAO empleadoDAO = new EmpleadoDAO(rolDAO);

        EstadoUsuarioDAO estadoUsuarioDAO = new EstadoUsuarioDAO(); // idem
        UsuarioDAO usuarioDAO = new UsuarioDAO(empleadoDAO, estadoUsuarioDAO);

        ServicioDeAutentificacion servicio = new ServicioDeAutentificacion(usuarioDAO);

        try {
            // 1. Crear empleado
            System.out.println("Creando nuevo usuario");
            System.out.print("id del empleado: ");
            int id = sc.nextInt();
            sc.nextLine();


            Empleado empleado = empleadoDAO.buscarPorId(id);
            System.out.println("Empleado guardado con ID: " + empleado.getId());

            // 2. Crear usuario
            System.out.println("Creando usuario para empleado");

            System.out.print("Nombre usuario: ");
            String nombreUsuario = sc.nextLine();

            if (servicio.existeUsuario(nombreUsuario)) {
                System.out.println("El nombre de usuario ya existe. Intenta con otro.");
                return;
            }

            System.out.print("Contraseña: ");
            String contraseña = sc.nextLine();

            // Hashear contraseña
            String hash = ServicioDeAutentificacion.hashContraseña(contraseña);

            Usuario usuario = Usuario.builder()
                    .nombreUsuario(nombreUsuario)
                    .empleado(empleado)
                    .hashContraseña(hash)
                    .build();

            usuario = usuarioDAO.guardar(usuario);

            System.out.println("Usuario guardado con ID: " + usuario.getId());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}
