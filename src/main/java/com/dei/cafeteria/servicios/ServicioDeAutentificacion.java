package com.dei.cafeteria.servicios;

import com.dei.cafeteria.dao.UsuarioDAO;
import com.dei.cafeteria.modelo.Usuario;

import java.util.Optional;

public class ServicioDeAutentificacion {
    private final UsuarioDAO usuarioDao;
    private Usuario usuarioActual;

    public void AuthService(UsuarioDAO usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public ServicioDeAutentificacion(UsuarioDAO usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean login(String username, String password) {
        Optional<Usuario> usuario = usuarioDao.buscarPorUsername(username);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            this.usuarioActual = usuario.get();
            return true;
        }
        return false;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public boolean tieneRol(Rol rol) {
        return usuarioActual != null && usuarioActual.getRol() == rol;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
