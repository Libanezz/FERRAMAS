package com.ferramas.marca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "marca")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Long idMarca;

    private String nombre;

    // Constructor vacío
    public Marca() {}

    // Constructor completo
    public Marca(Long idMarca, String nombre) {
        this.idMarca = idMarca;
        this.nombre = nombre;
    }

    // Getters & Setters
    public Long getIdMarca() { return idMarca; }
    public void setIdMarca(Long idMarca) { this.idMarca = idMarca; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}