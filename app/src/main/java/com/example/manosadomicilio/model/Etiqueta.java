package com.example.manosadomicilio.model;

public class Etiqueta {
    private int id;
    private String nombre;

    public Etiqueta(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
}
