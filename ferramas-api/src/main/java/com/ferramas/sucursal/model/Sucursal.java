package com.ferramas.sucursal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long idSucursal;

    private String nombre;
    private String direccion;

    private String comuna;
    private String region;

    public Sucursal() {}

    public Sucursal(Long idSucursal, String nombre, String direccion, String comuna, String region) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.comuna = comuna;
        this.region = region;
    }

    public Long getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Long idSucursal) { this.idSucursal = idSucursal; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
