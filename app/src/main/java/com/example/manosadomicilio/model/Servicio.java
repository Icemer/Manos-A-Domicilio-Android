package com.example.manosadomicilio.model;

import java.math.BigDecimal;

public class Servicio {
    private int id;
    private int usuarioId;
    private int trabajadorId;
    private int categoriaId;
    private int direccionClienteId;
    private String fecha;
    private String horaInicial;
    private String descripcion;
    private String estado;
    private BigDecimal precio;
    private Integer calificacionCliente;
    private Integer calificacionTrabajador;
    private String nombreTrabajador; // Nuevo campo

    public Servicio(int id, int usuarioId, int trabajadorId, int categoriaId, int direccionClienteId, String fecha, String horaInicial, String descripcion, String estado, BigDecimal precio, Integer calificacionCliente, Integer calificacionTrabajador) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.trabajadorId = trabajadorId;
        this.categoriaId = categoriaId;
        this.direccionClienteId = direccionClienteId;
        this.fecha = fecha;
        this.horaInicial = horaInicial;
        this.descripcion = descripcion;
        this.estado = estado;
        this.precio = precio;
        this.calificacionCliente = calificacionCliente;
        this.calificacionTrabajador = calificacionTrabajador;
    }

    // Getters
    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getTrabajadorId() { return trabajadorId; }
    public int getCategoriaId() { return categoriaId; }
    public int getDireccionClienteId() { return direccionClienteId; }
    public String getFecha() { return fecha; }
    public String getHoraInicial() { return horaInicial; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public BigDecimal getPrecio() { return precio; }
    public Integer getCalificacionCliente() { return calificacionCliente; }
    public Integer getCalificacionTrabajador() { return calificacionTrabajador; }
    public String getNombreTrabajador() { return nombreTrabajador; } // Nuevo getter

    // Setters
    public void setNombreTrabajador(String nombreTrabajador) { this.nombreTrabajador = nombreTrabajador; } // Nuevo setter
}
