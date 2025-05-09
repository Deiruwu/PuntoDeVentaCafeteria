package com.dei.cafeteria.servicios;

import com.dei.cafeteria.dao.DAOException;
import com.dei.cafeteria.dao.UsuarioDAO;
import com.dei.cafeteria.modelo.Usuario;
import com.dei.cafeteria.modelo.EstadoUsuario;

import java.sql.Timestamp;
import java.util.Date;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;

/**
 * Servicio que maneja la autenticación de usuarios usando BCrypt.
 */
public class ServicioDeAutentificacion {
    private static final int ID_ESTADO_ACTIVO = 1;
    private static final int ID_ESTADO_INACTIVO = 2;
    private final UsuarioDAO usuarioDAO;

    public ServicioDeAutentificacion(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario autenticar(String nombreUsuario, String contraseña) throws ServicioException {
        try {
            Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);

            if (usuario == null) {
                return null;
            }

            if (usuario.getEstado().getId() != ID_ESTADO_ACTIVO) {
                throw new ServicioException("El usuario está " + usuario.getEstado().getNombre());
            }

            // Verificar contraseña con BCrypt
            if (BCrypt.checkpw(contraseña, usuario.getHashContraseña())) {
                Timestamp ahora = new Timestamp(System.currentTimeMillis());
                usuarioDAO.actualizarUltimoAcceso(usuario.getId(), ahora);
                return usuario;
            } else {
                return null; // Contraseña incorrecta
            }
        } catch (DAOException e) {
            throw new ServicioException("Error al autenticar: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ServicioException("Formato de hash inválido: " + e.getMessage(), e);
        }
    }

    // Metodo para generar hash de contraseña con BCrypt (útil al crear/actualizar usuarios)
    public static String hashContraseña(String contraseña) {
        return BCrypt.hashpw(contraseña, BCrypt.gensalt());
    }

    public boolean existeUsuario(String nombreUsuario) throws ServicioException {
        try {
            return usuarioDAO.buscarPorNombreUsuario(nombreUsuario) != null;
        } catch (DAOException e) {
            throw new ServicioException("Error al verificar usuario: " + e.getMessage(), e);
        }
    }
}