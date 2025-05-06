package com.dei.cafeteria.modelo;

public class Empleado {
    private int id;
    private String nombre;
    private RolEmpleado rol;

    public Empleado(int id, String nombre, RolEmpleado rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
