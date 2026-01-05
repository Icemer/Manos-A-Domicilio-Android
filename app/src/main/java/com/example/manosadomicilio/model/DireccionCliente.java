package com.example.manosadomicilio.model;

public class DireccionCliente {
    private int id;
    private int usuarioId;
    private int zonaId;
    private String calle;
    private String numero;
    private String colonia;
    private String pais;
    private String estado;
    private String municipio;
    private String referencias;

    public DireccionCliente(int id, int usuarioId, int zonaId, String calle, String numero, String colonia, String pais, String estado, String municipio, String referencias) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.zonaId = zonaId;
        this.calle = calle;
        this.numero = numero;
        this.colonia = colonia;
        this.pais = pais;
        this.estado = estado;
        this.municipio = municipio;
        this.referencias = referencias;
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getZonaId() { return zonaId; }
    public String getCalle() { return calle; }
    public String getNumero() { return numero; }
    public String getColonia() { return colonia; }
    public String getPais() { return pais; }
    public String getEstado() { return estado; }
    public String getMunicipio() { return municipio; }
    public String getReferencias() { return referencias; }
}
