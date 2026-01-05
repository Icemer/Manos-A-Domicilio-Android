package com.example.manosadomicilio.model;

public class Ticket {
    private int id;
    private int servicioId;
    private String descripcion;
    private String estado;

    public Ticket(int id, int servicioId, String descripcion, String estado) {this.id = id;
        this.servicioId = servicioId;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public int getServicioId() { return servicioId; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
}
