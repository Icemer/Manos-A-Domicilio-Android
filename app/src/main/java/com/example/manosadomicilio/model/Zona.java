package com.example.manosadomicilio.model;

import androidx.annotation.NonNull;

public class Zona {
    private int id;
    private String nombre;
    private boolean isSelected;

    public Zona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.isSelected = false;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}
