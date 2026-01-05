package com.example.manosadomicilio.model;

import java.math.BigDecimal;

public class Pago {
    private int id;
    private int servicioId;
    private BigDecimal monto;
    private String estado;
    private String metodoPago;

    public Pago(int id, int servicioId, BigDecimal monto, String estado, String metodoPago) {
        this.id = id;
        this.servicioId = servicioId;
        this.monto = monto;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public int getId() { return id; }
    public int getServicioId() { return servicioId; }
    public BigDecimal getMonto() { return monto; }
    public String getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }
}
