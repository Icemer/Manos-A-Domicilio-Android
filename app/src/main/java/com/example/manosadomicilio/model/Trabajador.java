package com.example.manosadomicilio.model;

public class Trabajador {
    private int id;
    private int usuarioId;
    private int categoriaId;
    private boolean disponibilidad;
    private String descripcion;
    private String nombreUsuario;

    public Trabajador(int id, int usuarioId, int categoriaId, boolean disponibilidad, String descripcion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.disponibilidad = disponibilidad;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getCategoriaId() { return categoriaId; }
    public boolean isDisponibilidad() { return disponibilidad; }
    public String getDescripcion() { return descripcion; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}
