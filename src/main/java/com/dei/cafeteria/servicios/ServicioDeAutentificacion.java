package com.dei.cafeteria.servicios;

import com.dei.cafeteria.dao.UsuarioDAO;
import com.dei.cafeteria.modelo.Usuario;

import java.util.Optional;

public class ServicioDeAutentificacion {
    private final UsuarioDAO usuarioDao;
    private Usuario usuarioActual;

    public ServicioDeAutentificacion(UsuarioDAO usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean login(String username, String passwordHash) {
        try {
            // Intentar autenticar y obtener el usuario desde el Optional
            Optional<Usuario> usuarioOpt = usuarioDao.autenticar(username, passwordHash);

            // Si el usuario está presente, lo asignamos a usuarioActual
            if (usuarioOpt.isPresent()) {
                this.usuarioActual = usuarioOpt.get();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean estaAutenticado() {
        return usuarioActual != null;
    }
}
