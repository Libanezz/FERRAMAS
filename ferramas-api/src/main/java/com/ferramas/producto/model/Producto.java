package com.ferramas.producto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.ferramas.marca.model.Marca;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;

    @Column(name = "url_imagen")
    private String urlImagen;

    private Boolean estado = true;

    @ManyToOne
    @JoinColumn(name = "id_marca")
    private Marca marca;

    // Constructor void
    public Producto() {
    }

    // Constructor
    public Producto(Long idProducto, String nombre, String descripcion, BigDecimal precio, String urlImagen, Boolean estado, Marca marca) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.urlImagen = urlImagen;
        this.estado = estado;
        this.marca = marca;
    }

    // Getters & Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }
}