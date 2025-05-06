package com.dei.cafeteria.modelo;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String contraseña;
    private Empleado empleado;

    public Usuario(int id, String nombreUsuario, String contraseña, Empleado empleado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.empleado = empleado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public String getContraseña() {
        return contraseña;
    }

    public int getId() {
        return id;
    }
}
